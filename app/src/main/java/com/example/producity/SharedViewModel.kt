package com.example.producity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SharedViewModel: ViewModel() {
    val currentUser : MutableLiveData<User> = MutableLiveData(User())
    val userImage: MutableLiveData<String> = MutableLiveData("")

    fun getUser(): User {
        return currentUser.value!!
    }

    fun updateUser(user: User) {
        currentUser.value = user
    }

    fun loadUserImage() {
        Firebase.storage.getReference("profile_pictures/${currentUser.value!!.username}")
            .downloadUrl
            .addOnSuccessListener {  uri ->
                if(uri == null) return@addOnSuccessListener

                userImage.value = uri.toString()
            }
    }



}