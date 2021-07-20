package com.example.producity.models.source

interface IAuthRepository {

    fun createUserWithEmailAndPassword(email: String, password: String): String
    fun signInWithEmailAndPassword(email: String, password: String): Boolean
    fun verifyPassword(pass: String): Boolean
    fun changeEmail(email: String)
    fun changePassword(pass: String)

}