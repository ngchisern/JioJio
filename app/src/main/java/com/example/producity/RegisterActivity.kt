package com.example.producity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.producity.models.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    companion object {
        val BLANK_PROFILE_IMG_URL =
            "https://firebasestorage.googleapis.com/v0/b/orbital-7505e.appspot.com/o/profile_pictures%2Fblank-profile-picture.png?alt=media&token=cfaa6afb-1651-4563-9654-a0d6f14fdffc"
    }

    private val registerViewModel: RegisterViewModel by viewModels() {
        RegisterViewModelFactory(ServiceLocator.provideAuthRepository(), ServiceLocator.provideUserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val signUpButton: Button = findViewById(R.id.sign_up_button)
        val goToSignIn: TextView = findViewById(R.id.sign_in_text)

        signUpButton.setOnClickListener {
            signUp()
        }

        goToSignIn.setOnClickListener {
            finish()
        }
    }

    private fun signUp() {
        val username = findViewById<EditText>(R.id.signup_username)
        val email = findViewById<EditText>(R.id.sign_up_email)
        val password = findViewById<EditText>(R.id.sign_up_password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)

        var isValid = true

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            if(email.text.toString().isEmpty()) {
                email.setError("Please enter your email.")
            } else {
                email.setError("Wrong email format.")
            }
            isValid = false
        }

        if(password.text.toString().length < 6) {
            password.setError("Password must contain at least 6 characters.")
            isValid = false
        }

        if (password.text.toString() != confirmPassword.text.toString()) {
            confirmPassword.setError("Passwords do not match.")
            isValid = false
        }


        if (username.text.toString().isEmpty()) {
            username.error = "Please enter your username."
            isValid = false
        }


        if(isValid) {
            val checkTask = registerViewModel.isUsernameTaken(username.text.toString())

            if (checkTask) {
                username.error = "The username is already taken."
                return
            }

            registerViewModel.createUser(
                username.text.toString(),
                email.text.toString(),
                password.text.toString()
            )


            val intent = Intent(this, MainActivity::class.java)
            /* TODO Uncomment to display sent email verification page
            val intent = Intent(this, SentEmailVerificationActivity::class.java)
            */
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }


    }



}