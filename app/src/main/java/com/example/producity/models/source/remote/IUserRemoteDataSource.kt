package com.example.producity.models.source.remote

import android.net.Uri
import com.example.producity.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface IUserRemoteDataSource {
    fun createUser(username: String, uid: String)
    fun isUsernmeTaken(username: String): Task<DocumentSnapshot>
    suspend fun loadUserProfile(username: String): User
    suspend fun editUserProfile(editedUserProfile: User)
    suspend fun loadFriends(username: String): List<User>
    suspend fun addFriend(currUser: User, friend: User)
    suspend fun deleteFriend(currUser: User, friend: User)
    suspend fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String
    suspend fun getProfilePicUrl(username: String): String
    suspend fun updateProfileInFriends(editedUserProfile: User, friendUsername: String)
    suspend fun uploadImageToFirebaseStorageAndEditProfile(imageUri: Uri, userProfile: User): User
}