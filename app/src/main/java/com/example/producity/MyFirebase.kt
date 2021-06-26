package com.example.producity

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object MyFirebase {

    val auth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage
}