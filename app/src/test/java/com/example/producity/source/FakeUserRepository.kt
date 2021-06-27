package com.example.producity.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.producity.models.User
import com.example.producity.models.source.IUserRepository
import com.example.producity.models.source.remote.IUserRemoteDataSource
import com.example.producity.ui.profile.IProfileRepository

class FakeUserRepository(private val userRemoteDataSource: IUserRemoteDataSource) :
    IUserRepository {
    override fun createUser(username: String, uid: String) {
        TODO("Not yet implemented")
    }

    override fun isUsernameTaken(username: String): Boolean {
        TODO("Not yet implemented")
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
        return userRemoteDataSource.uploadImageToFirebaseStorageAndEditProfile(
            imageUri,
            userProfile
        )
    }
}