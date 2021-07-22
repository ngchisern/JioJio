package com.example.producity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.producity.models.Participant
import com.example.producity.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import timber.log.Timber
import java.io.File

class GoogleLoginRegisterUsernameActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels() {
        RegisterViewModelFactory(
            ServiceLocator.provideAuthRepository(),
            ServiceLocator.provideUserRepository()
        )
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login_register_username)

        auth = Firebase.auth

        val signUpButton: Button = findViewById(R.id.google_sign_up_button)

        signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val usernameEditText: EditText = findViewById(R.id.google_register_username_edittext)

        var isValid = true

        if (usernameEditText.text.toString().isEmpty()) {
            usernameEditText.error = "Please enter your username."
            isValid = false
        }

        if(usernameEditText.text.length < 6) {
            usernameEditText.error = "Username must contain at least 6 characters."
            isValid = false
        }

        if (isValid) {
            val checkTask = registerViewModel.isUsernameTaken(usernameEditText.text.toString())

            if (checkTask) {
                usernameEditText.error = "The username is already taken."
                return
            }

            val storage = Firebase.storage
            val rtdb = Firebase.database
            val db = Firebase.firestore

            val username = usernameEditText.text.toString()

            val user = User(
                usernameEditText.text.toString(),
                auth.currentUser!!.uid,
                usernameEditText.text.toString(),
                bio = "Hello there.",
                banner = "https://media.giphy.com/media/bcKmIWkUMCjVm/giphy.gif"
            )

            val uri = Uri.parse("android.resource://" + packageName + "/" + R.drawable.the_beginning_of_something_new)


            storage.getReference("/profile_pictures/$username")
                .putFile(uri)
                .addOnFailureListener {
                    Log.d("Main", it.message.toString())
                }

            rtdb.getReference("participant/$username")
                .setValue(Participant(username, username, mapOf(), listOf()))

            db.document("users/$username")
                .set(user)
                .addOnCompleteListener {
                    Timber.d("Done")
                }
                .addOnSuccessListener {
                    Timber.d("Successfully sign up!")
                }

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(USER, user)

            startActivity(intent)
        }
    }
}