package com.example.producity.models

class User(
    val username: String,
    val uid: String,
    val displayName: String,
    val telegramHandle: String,
    val gender: String,
    val birthday: String,
    val bio: String,
    val imageUrl: String
) {
    constructor() : this(
        "", "", "",
        "", "", "", "",
        "https://firebasestorage.googleapis.com/v0/b/jiojio-6a358.appspot.com/o/profile_pictures%2Fblank-profile-picture.png?alt=media&token=7b7b2083-ee3f-493e-bcaf-bab365427694"
    ) // the image is a blank profile picture

}