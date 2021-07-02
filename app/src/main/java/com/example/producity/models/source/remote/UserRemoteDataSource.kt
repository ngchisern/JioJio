package com.example.producity.models.source.remote

import android.net.Uri
import android.util.Log
import com.example.producity.MyFirebase
import com.example.producity.RegisterActivity
import com.example.producity.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val TAG = "UserRemoteDataSource"

class UserRemoteDataSource : IUserRemoteDataSource {

    private val db = MyFirebase.db
    private val storage = MyFirebase.storage

    override fun createUser(username: String, uid: String) {
        val user = User(
            username, uid, "",
            "", "", "", "",
            RegisterActivity.BLANK_PROFILE_IMG_URL
        )

        db.document("users/$username")
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d("Main", "Successfully sign up!")
            }
            .addOnFailureListener { e ->
                Log.d("Main", "Error adding document", e)
            }
    }

    override fun isUsernmeTaken(username: String): Task<DocumentSnapshot> {

        return db.document("users/$username")
            .get()
            .addOnSuccessListener {
                Log.d("Main", it.exists().toString())
            }

    }

    override suspend fun loadUserProfile(username: String): User {
        var userProfile = User()

        db.collection("users")
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
                }
            }

        return userProfile
    }

    override suspend fun editUserProfile(editedUserProfile: User) {
        db.collection("users")
            .document(editedUserProfile.username)
            .set(editedUserProfile)
            .addOnSuccessListener { // get friends and update the profile details in friends
                Log.d(TAG, "edited user profile")

                // get list of friends
                val friendList: MutableList<User> = mutableListOf()
                val currUsername = editedUserProfile.username
                db.collection("users/$currUsername/friends")
                    .orderBy("username")
                    .get()
                    .addOnSuccessListener {
                        it.forEach { doc ->
                            val friend = doc.toObject(User::class.java)
                            friendList.add(friend)
                        }

                        // update the current user profile details in each of the friends
                        val friendUsernames: List<String> =
                            friendList.map { it.username }
                        friendUsernames.forEach { friendUsername ->
                            db.collection("users/$friendUsername/friends")
                                .document(currUsername)
                                .set(editedUserProfile)
                                .addOnSuccessListener {
                                    Log.d(TAG, "updated profile in friend: $friendUsername")
                                }
                                .addOnFailureListener {
                                    Log.d(TAG, it.toString())
                                }
                        }
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    override suspend fun loadFriends(username: String): List<User> {
        return try {
            db.collection("users/$username/friends")
                .orderBy("username")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            Log.d(TAG, "Error getting friends: $e")
            listOf()
        }
    }

    override suspend fun addFriend(currUser: User, friend: User) {
        db.document("users/${friend.username}")
            .get()
            .addOnSuccessListener {
                if (it == null || !it.exists()) {
                    Log.d(TAG, "No doc")
                    return@addOnSuccessListener
                }

                db.document("users/${currUser.username}/friends/${friend.username}")
                    .set(friend)

                db.document("users/${friend.username}/friends/${currUser.username}")
                    .set(currUser)

            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    override suspend fun deleteFriend(currUser: User, friend: User) {
        db.document("users/${currUser.username}/friends/${friend.username}")
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Deleted ${currUser.username}'s friend: ${friend.username}")
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }

        db.document("users/${friend.username}/friends/${currUser.username}")
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Deleted ${friend.username}'s friend: ${currUser.username}")
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    override suspend fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String {
        val ref = storage.getReference("/profile_pictures/$username")

        var url = ""

        ref.putFile(imageUri)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    url = it.toString()
                }
            }

        return url
    }


    override suspend fun getProfilePicUrl(username: String): String {
        val ref = storage.getReference("/profile_pictures/$username")

        var url = ""

        ref.downloadUrl.addOnSuccessListener {
            url = it.toString()
        }

        return url
    }

    override suspend fun updateProfileInFriends(editedUserProfile: User, friendUsername: String) {
        db.collection("users/$friendUsername/friends")
            .document(editedUserProfile.username)
            .set(editedUserProfile)
            .addOnSuccessListener {
                Log.d(TAG, "updated profile in friend: $friendUsername")
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }


    override suspend fun uploadImageToFirebaseStorageAndEditProfile(
        imageUri: Uri,
        userProfile: User
    ): User {
        val ref = storage.getReference("/profile_pictures/${userProfile.username}")

        var newUrl = userProfile.imageUrl // original url, to be updated later

        val editedUserProfile = User(
            userProfile.username,
            userProfile.uid,
            userProfile.displayName,
            userProfile.telegramHandle,
            userProfile.gender,
            userProfile.birthday,
            userProfile.bio
        )

        val task = ref.putFile(imageUri) // upload image to Firebase Storage
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image at: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    newUrl = it.toString()
                    Log.d(TAG, "Downloaded new image url: $newUrl")

                    editedUserProfile.imageUrl = newUrl

                    val currUsername = editedUserProfile.username
                    // Update the user profile details
                    db.collection("users")
                        .document(currUsername)
                        .set(editedUserProfile)
                        .addOnSuccessListener { // get friends and update the profile details in friends
                            Log.d(TAG, "Edited user profile")

                            // get list of friends
                            val friendList: MutableList<User> = mutableListOf()
                            db.collection("users/$currUsername/friends")
                                .orderBy("username")
                                .get()
                                .addOnSuccessListener {
                                    it.forEach { doc ->
                                        val friend = doc.toObject(User::class.java)
                                        friendList.add(friend)
                                    }

                                    // update the current user profile details in each of the friends
                                    val friendUsernames: List<String> =
                                        friendList.map { it.username }
                                    friendUsernames.forEach { friendUsername ->
                                        db.collection("users/$friendUsername/friends")
                                            .document(currUsername)
                                            .set(editedUserProfile)
                                            .addOnSuccessListener {
                                                Log.d(
                                                    TAG,
                                                    "updated profile in friend: $friendUsername"
                                                )
                                            }
                                            .addOnFailureListener {
                                                Log.d(TAG, it.toString())
                                            }
                                    }
                                }
                        }


                }
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }

        while (!task.isComplete) {
        }

        return editedUserProfile
    }


}