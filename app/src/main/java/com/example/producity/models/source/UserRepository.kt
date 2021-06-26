package com.example.producity.models.source

import android.net.Uri
import com.example.producity.models.User
import com.example.producity.models.source.remote.IUserRemoteDataSource

class UserRepository(private val userRemoteDataSource: IUserRemoteDataSource) : IUserRepository {

    override fun loadUserProfile(username: String): User {
        return userRemoteDataSource.loadUserProfile(username)
    }

    override fun editUserProfile(editedUserProfile: User) {
        userRemoteDataSource.editUserProfile(editedUserProfile)
    }

    override fun loadFriends(username: String): List<User> {
        return userRemoteDataSource.loadFriends(username)
    }

    override fun uploadImageToFirebaseStorage(imageUri: Uri, username: String) {
        userRemoteDataSource.uploadImageToFirebaseStorage(imageUri, username)
    }

    override fun updateProfileInFriends(editedUserProfile: User, friendUsername: String) {
        userRemoteDataSource.updateProfileInFriends(editedUserProfile, friendUsername)
    }

}