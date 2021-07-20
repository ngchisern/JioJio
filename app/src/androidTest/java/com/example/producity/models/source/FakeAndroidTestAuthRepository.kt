package com.example.producity.models.source

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class FakeAndroidTestAuthRepository: IAuthRepository {
    private var authData: LinkedHashMap<String, String> = LinkedHashMap()

    override fun createUserWithEmailAndPassword(email: String, password: String): String {
        authData.put(email, password)
        return "random"
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        return authData[email] == password
    }

    override fun verifyPassword(pass: String): Boolean {
        return true
    }

    override fun changeEmail(email: String) {

    }

    override fun changePassword(pass: String) {

    }

    fun addAccount(email:String, password: String) {
        authData.put(email, password)
    }
}