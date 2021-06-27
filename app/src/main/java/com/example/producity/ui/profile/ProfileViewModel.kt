package com.example.producity.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.producity.RegisterActivity
import com.example.producity.models.User
import com.example.producity.models.source.IUserRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: IUserRepository) : ViewModel() {

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
//        var editedUserProfile = User()
//        viewModelScope.launch {
//            _currentUser = userRepository.uploadImageToFirebaseStorageAndEditProfile(
//                imageUri, userProfile
//            )
//        }
        val returnedUser: Deferred<User> = viewModelScope.async {
            userRepository.uploadImageToFirebaseStorageAndEditProfile(
                imageUri, userProfile
            )
        }
        val result: User = returnedUser.await()
        Log.d("PROFILE_VIEW_MODEL", "RESULT: $result, username: ${result.username}")
        return result
//        return returnedUser.await()
//        return editedUserProfile
    }

//    private var _currentUser = User()
//    val currentUser: User = _currentUser
}


@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val userRepository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (ProfileViewModel(userRepository) as T)
    }
}