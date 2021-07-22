package com.example.producity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.producity.databinding.ActivityLaunchappBinding
import com.example.producity.models.User
import com.giphy.sdk.core.models.enums.MediaType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class LaunchAppActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityLaunchappBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLaunchappBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        lifecycleScope.launch {
            delay(1000)
            verifyUser()
        }
    }

    private fun verifyUser() {
        val currentUser = auth.currentUser


        if (currentUser == null) {
            goToLogIn()
            return
        }

        db.collection("users")
            .whereEqualTo("uid", currentUser.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    goToLogIn()
                    return@addOnSuccessListener
                }

                it.forEach { doc ->
                    val user = doc.toObject(User::class.java)
                    goToMainScreen(user)
                }
            }
    }

    private fun goToLogIn() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun goToMainScreen(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(USER, user)

        startActivity(intent)
    }

}