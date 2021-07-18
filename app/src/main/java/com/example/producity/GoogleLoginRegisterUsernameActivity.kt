package com.example.producity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth

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

        auth = MyFirebase.auth

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

        if (isValid) {
            val checkTask = registerViewModel.isUsernameTaken(usernameEditText.text.toString())

            if (checkTask) {
                usernameEditText.error = "The username is already taken."
                return
            }

            registerViewModel.createUserInFirestore(
                usernameEditText.text.toString(), auth.currentUser!!.uid
            )

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}