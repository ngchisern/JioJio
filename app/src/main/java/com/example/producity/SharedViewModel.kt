package com.example.producity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.User

class SharedViewModel: ViewModel() {

    val currentUser : MutableLiveData<User> = MutableLiveData(User())

    fun updateUser(user: User) {
        currentUser.value = user
    }
}