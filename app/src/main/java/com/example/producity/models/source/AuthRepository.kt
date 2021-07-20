package com.example.producity.models.source

import android.net.Uri
import android.util.Log
import com.example.producity.MyFirebase
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.CredentialsProvider
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthRepository: IAuthRepository {

    private val auth = Firebase.auth

    override fun createUserWithEmailAndPassword(email: String, password: String): String {

        val task = auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Main", "${task.exception}")
                }
                /* TODO Uncomment to send email verification when creating new account
                else {
                    auth.currentUser!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("AuthRepository", "Created account and sent email verification.")
                            } else {
                                Log.d("AuthRepository", task.exception.toString())
                            }
                        }
                }
                */
            }

        while(!task.isComplete) {

        }

        Log.d("Main", task.isComplete.toString())

        return auth.currentUser!!.uid

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

    override fun verifyPassword(pass: String): Boolean {
        val user = Firebase.auth.currentUser!!
        val credential = EmailAuthProvider.getCredential(user.email!!, pass)

        val task = user.reauthenticate(credential)

        while(!task.isComplete) {

        }

        return task.isSuccessful
    }

    override fun changeEmail(email: String) {
        Firebase.auth.currentUser!!.updateEmail(email)
    }

    override fun changePassword(pass: String) {
        Firebase.auth.currentUser!!.updatePassword(pass)
    }




}