package com.example.producity.models.source

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

object AuthRepository : IAuthRepository {

    private val auth = Firebase.auth

    override fun createUserWithEmailAndPassword(email: String, password: String): String {

        val task = auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.d("${task.exception}")
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

        Timber.d(task.isComplete.toString())

        return ""

    }

    override fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            return false
        }

        val task = auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("signInWithEmail:success")
                } else {
                    Timber.d("signInWithEmail:failure")
                }
            }
            .addOnFailureListener {

            }

        while (!task.isComplete) {

        }

        return task.isSuccessful
    }

    override fun verifyPassword(pass: String): Boolean {
        val user = Firebase.auth.currentUser!!
        val credential = EmailAuthProvider.getCredential(user.email!!, pass)

        val task = user.reauthenticate(credential)

        while (!task.isComplete) {

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