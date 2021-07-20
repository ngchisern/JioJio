package com.example.producity.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.producity.RegisterActivity
import com.example.producity.models.User
import com.example.producity.models.source.IAuthRepository
import com.example.producity.models.source.IUserRepository
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel(
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    fun getUserProfile(username: String): User {
        var user = User()
        viewModelScope.launch { user = userRepository.loadUserProfile(username) }
        return user
    }

    fun updateUserProfile(userProfile: User) {
        viewModelScope.launch { userRepository.editUserProfile(userProfile) }
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String {
        var newImageUrl = RegisterActivity.BLANK_PROFILE_IMG_URL

        viewModelScope.launch {
            newImageUrl = userRepository.uploadImageToFirebaseStorage(imageUri, username)
        }

        return newImageUrl
    }

    fun getProfilePicUrl(username: String): String {
        var url = RegisterActivity.BLANK_PROFILE_IMG_URL
        viewModelScope.launch { url = userRepository.getProfilePicUrl(username) }
        return url
    }

    fun updateProfileInFriends(editedUserProfile: User, friendUsername: String) {
        viewModelScope.launch {
            userRepository.updateProfileInFriends(
                editedUserProfile,
                friendUsername
            )
        }
    }

    fun loadFriends(username: String): List<User> {
        var friends = listOf<User>()
        viewModelScope.launch { friends = userRepository.loadFriends(username) }
        return friends
    }

    suspend fun uploadImageToFirebaseStorageAndEditProfile(
        imageUri: Uri,
        userProfile: User
    ): User {
        val returnedUser: Deferred<User> = viewModelScope.async {
            userRepository.uploadImageToFirebaseStorageAndEditProfile(
                imageUri, userProfile
            )
        }
        val result: User = returnedUser.await()
        Timber.d("RESULT: $result, username: ${result.username}")
        return result
    }

    fun verifyPassword(pass: String): Boolean {
        return authRepository.verifyPassword(pass)
    }

    fun changeEmail(email: String) {
        authRepository.changeEmail(email)
    }

    fun changePassword(pass: String) {
        authRepository.changePassword(pass)
    }

    fun updateDatabase(username: String, nickname: String) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/$username")
            .updateChildren(mapOf("nickname" to nickname))
    }


}


@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (ProfileViewModel(userRepository, authRepository) as T)
    }
}