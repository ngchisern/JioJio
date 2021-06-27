package com.example.producity.models.source

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.producity.MainActivity
import com.example.producity.MyFirebase
import com.example.producity.RegisterActivity
import com.example.producity.models.User
import com.example.producity.models.source.remote.IUserRemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserRepository(private val userRemoteDataSource: IUserRemoteDataSource) : IUserRepository {

    override fun createUser(username: String, uid: String) {
        userRemoteDataSource.createUser(username, uid)
    }

    override fun isUsernameTaken(username: String): Boolean {
        val task = userRemoteDataSource.isUsernmeTaken(username)

        while(!task.isComplete) {

        }

        return task.result!!.exists()
    }

    override suspend fun loadUserProfile(username: String): User {
        return userRemoteDataSource.loadUserProfile(username)
    }

    override suspend fun editUserProfile(editedUserProfile: User) {
        userRemoteDataSource.editUserProfile(editedUserProfile)
    }

    override suspend fun loadFriends(username: String): List<User> {
        return userRemoteDataSource.loadFriends(username)
    }

    override suspend fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String {
        return userRemoteDataSource.uploadImageToFirebaseStorage(imageUri, username)
    }

    override suspend fun getProfilePicUrl(username: String): String {
        return userRemoteDataSource.getProfilePicUrl(username)
    }

    override suspend fun updateProfileInFriends(editedUserProfile: User, friendUsername: String) {
        userRemoteDataSource.updateProfileInFriends(editedUserProfile, friendUsername)
    }

    override suspend fun uploadImageToFirebaseStorageAndEditProfile(
        imageUri: Uri,
        userProfile: User
    ): User {
        return userRemoteDataSource.uploadImageToFirebaseStorageAndEditProfile(imageUri, userProfile)
    }

}