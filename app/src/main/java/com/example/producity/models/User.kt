package com.example.producity.models

import com.example.producity.RegisterActivity


class User(
    val username: String = "",
    val uid: String = "",
    val displayName: String = "",
    val telegramHandle: String = "",
    val gender: String = "",
    val birthday: String = "",
    val bio: String = "",
    var imageUrl: String = RegisterActivity.BLANK_PROFILE_IMG_URL
)