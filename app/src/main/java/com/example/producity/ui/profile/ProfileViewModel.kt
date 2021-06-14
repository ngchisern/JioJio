package com.example.producity.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.User

class ProfileViewModel : ViewModel() {

    val currentUserProfile: MutableLiveData<User> = MutableLiveData(User())

    var selectedGender: String = currentUserProfile.value?.gender.toString()
    // used to update gender in database

    fun updateUserProfile(userProfile: User) {
        currentUserProfile.value = userProfile
    }

}