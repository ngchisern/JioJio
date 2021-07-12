package com.example.producity.models.source

import android.net.Uri
import com.example.producity.models.User

interface IUserRepository {
    fun createUser(username: String, uid: String)
    fun isUsernameTaken(username: String): Boolean
    suspend fun checkUserExists(username: String): User?
    suspend fun loadUserProfile(username: String): User
    suspend fun editUserProfile(editedUserProfile: User)
    suspend fun loadFriends(username: String): List<User>
    suspend fun sendFriendRequest(sender: User, receiverUsername: String)
    suspend fun addFriend(currUser: User, friend: User)
    suspend fun deleteFriend(currUser: User, friend: User)
    suspend fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String
    suspend fun getProfilePicUrl(username: String): String
    suspend fun updateProfileInFriends(editedUserProfile: User, friendUsername: String)
    suspend fun uploadImageToFirebaseStorageAndEditProfile(imageUri: Uri, userProfile: User): User
}