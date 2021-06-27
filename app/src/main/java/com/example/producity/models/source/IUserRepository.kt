package com.example.producity.models.source

import android.net.Uri
import com.example.producity.models.User

interface IUserRepository {
    suspend fun loadUserProfile(username: String): User
    suspend fun editUserProfile(editedUserProfile: User)
    suspend fun loadFriends(username: String): List<User>
    suspend fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String
    suspend fun getProfilePicUrl(username: String): String
    suspend fun updateProfileInFriends(editedUserProfile: User, friendUsername: String)
    suspend fun uploadImageToFirebaseStorageAndEditProfile(imageUri: Uri, userProfile: User): User
}