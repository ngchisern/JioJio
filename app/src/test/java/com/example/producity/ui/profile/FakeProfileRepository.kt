package com.example.producity.ui.profile

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.User

class FakeProfileRepository : IProfileRepository {

    private val user: MutableLiveData<User> = MutableLiveData(User())

    override fun updateUserProfile(userProfile: User) {
        user.value = userProfile
    }

    override fun getUserProfile(): User {
        return user.value!!
    }
}