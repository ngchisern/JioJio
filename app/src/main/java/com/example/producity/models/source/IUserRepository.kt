package com.example.producity.models.source

import android.net.Uri
import com.example.producity.models.User

interface IUserRepository {
    fun loadUserProfile(username: String): User
    fun editUserProfile(editedUserProfile: User)
    fun loadFriends(username: String): List<User>
    fun uploadImageToFirebaseStorage(imageUri: Uri, username: String)
    fun updateProfileInFriends(editedUserProfile: User, friendUsername: String)
}