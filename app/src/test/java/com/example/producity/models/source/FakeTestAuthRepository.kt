package com.example.producity.models.source

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class FakeTestAuthRepository: IAuthRepository {

    var authData: LinkedHashMap<String, String> = LinkedHashMap()

    override fun createUserWithEmailAndPassword(email: String, password: String): String {
        authData.put(email, password)
        return "random"
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        return authData[email] == password
    }

    fun addAccount(email:String, password: String) {
        authData.put(email, password)
    }

}