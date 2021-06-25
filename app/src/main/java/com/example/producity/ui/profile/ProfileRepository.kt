package com.example.producity.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.producity.models.User

class ProfileRepository : IProfileRepository {

    private val _currentUserProfile: MutableLiveData<User> = MutableLiveData(User())
    val currentUserProfile: LiveData<User> = _currentUserProfile

    override fun updateUserProfile(userProfile: User) {
        _currentUserProfile.value = userProfile
    }

    override fun getUserProfile(): User {
        return currentUserProfile.value!!
    }
}