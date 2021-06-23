package com.example.producity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.producity.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    companion object {
        val BLANK_PROFILE_IMG_URL =
            "https://firebasestorage.googleapis.com/v0/b/orbital-7505e.appspot.com/o/profile_pictures%2Fblank-profile-picture.png?alt=media&token=cfaa6afb-1651-4563-9654-a0d6f14fdffc"
    }

    private val db = Firebase.firestore

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
        val username = findViewById<EditText>(R.id.display_name)
        val email = findViewById<EditText>(R.id.sign_up_email)
        val password = findViewById<EditText>(R.id.sign_up_password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)

        if (email.text.toString().isEmpty()) {
            email.setError("Please enter your email.")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.setError("Wrong email format.")
        } else if(password.text.toString().length < 6) {
            password.setError("Password must contain at least 6 characters.")
        } else if (password.text.toString() != confirmPassword.text.toString()) {
            confirmPassword.setError("Password does not match.")
        } else if (username.text.toString().isEmpty()) {
            username.setError("Please enter your username.")
        } else {
            db.document("users/${username.text}")
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        username.setError("Your username is already taken.")
                        return@addOnSuccessListener
                    }

                    createUser(
                        username.text.toString(),
                        email.text.toString(),
                        password.text.toString()
                    )
                }
        }



    }

    private fun createUser(username: String, email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Main", "${task.exception}")
                    return@addOnCompleteListener
                }
                val uid = FirebaseAuth.getInstance().uid ?: ""

                val user = User(
                    username, uid, "",
                    "", "", "", "",
                    BLANK_PROFILE_IMG_URL
                )

                db.document("users/$username")
                    .set(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Main", "Successfully sign up!")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Main", "Error adding document", e)
                    }

            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }


}