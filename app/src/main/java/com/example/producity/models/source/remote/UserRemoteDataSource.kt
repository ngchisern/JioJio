package com.example.producity.models.source.remote

import android.net.Uri
import android.util.Log
import com.example.producity.MyFirebase
import com.example.producity.models.User
import java.util.*

private const val TAG = "UserRemoteDataSource"

class UserRemoteDataSource : IUserRemoteDataSource {

    override fun loadUserProfile(username: String): User {
        var userProfile = User()

        MyFirebase.db.collection("users")
            .whereEqualTo("username", username)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if (it == null || it.isEmpty) {
                    Log.d(TAG, "cant set up user info")
                    return@addOnSuccessListener
                }

                it.forEach { doc ->
                    val temp = doc.toObject(User::class.java)
                    userProfile = temp
                    Log.d(TAG, "updated user")
                    val username = temp.username
                }
            }

        return userProfile
    }

    override fun editUserProfile(editedUserProfile: User) {
        MyFirebase.db.collection("users")
            .document(editedUserProfile.username)
            .set(editedUserProfile)
            .addOnSuccessListener {
                Log.d(TAG, "edited user profile")
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    override fun loadFriends(username: String): List<User> {
        val list: MutableList<User> = mutableListOf()

        MyFirebase.db.collection("users/$username/friends")
            .orderBy("username")
            .get()
            .addOnSuccessListener {
                it.forEach { doc ->
                    val friend = doc.toObject(User::class.java)
                    list.add(friend)
                }
            }

        return list
    }

    override fun uploadImageToFirebaseStorage(imageUri: Uri, username: String) {
        val storage = MyFirebase.storage
        val ref = storage.getReference("/profile_pictures/$username")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
            }
    }

    override fun updateProfileInFriends(editedUserProfile: User, friendUsername: String) {
        MyFirebase.db.collection("users/$friendUsername/friends")
            .document(editedUserProfile.username)
            .set(editedUserProfile)
            .addOnSuccessListener {
                Log.d(TAG, "updated profile in friend: $friendUsername")
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }
}