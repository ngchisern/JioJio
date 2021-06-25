package com.example.producity.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.User

class ProfileViewModel(private val profileRepository: IProfileRepository) : ViewModel() {

    var selectedGender: String = profileRepository.getUserProfile().gender
    // used to update gender in database

    fun updateUserProfile(userProfile: User) {
        profileRepository.updateUserProfile(userProfile)
    }

    fun getUserProfile(): User {
        return profileRepository.getUserProfile()
    }

}


@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val profileRepository: IProfileRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (ProfileViewModel(profileRepository) as T)
    }
}