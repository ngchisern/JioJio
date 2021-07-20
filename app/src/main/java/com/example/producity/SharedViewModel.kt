package com.example.producity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SharedViewModel : ViewModel() {
    val currentUser: MutableLiveData<User> = MutableLiveData(User())
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
            .addOnSuccessListener { uri ->
                userImage.value = uri.toString()
            }
            .addOnFailureListener {
                userImage.value =
                    "https://images.unsplash.com/photo-1600172454520-134a542a2255?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MjB8fHdoaXRlfGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&w=1000&q=80"
            }
    }


}