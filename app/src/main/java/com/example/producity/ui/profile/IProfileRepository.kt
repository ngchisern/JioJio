package com.example.producity.ui.profile

import com.example.producity.models.User

interface IProfileRepository {

    fun updateUserProfile(userProfile: User)
    fun getUserProfile(): User
}