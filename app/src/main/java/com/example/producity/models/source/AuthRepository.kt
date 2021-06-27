package com.example.producity.models.source

import android.net.Uri
import android.util.Log
import com.example.producity.MyFirebase
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthRepository: IAuthRepository {

    private val auth = Firebase.auth

    override fun createUserWithEmailAndPassword(email: String, password: String): String {

        val task = auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Main", "${task.exception}")
                }
            }

        while(!task.isComplete) {

        }

        Log.d("Main", task.isComplete.toString())

        return auth.uid!!

    }

    override fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        if(email.isEmpty() || password.isEmpty()) {
            return false
        }

        val task = auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Main", "signInWithEmail:success")
                } else {
                    Log.d("Main", "signInWithEmail:failure")
                }
            }
            .addOnFailureListener {

            }

        while(!task.isComplete) {
            // can add animation
        }

        return task.isSuccessful
    }


}