package com.example.producity.models.source

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface IAuthRepository {

    fun createUserWithEmailAndPassword(email: String, password: String): String
    fun signInWithEmailAndPassword(email: String, password: String): Boolean
    fun verifyPassword(pass: String): Boolean
    fun changeEmail(email:String)
    fun changePassword(pass: String)

}