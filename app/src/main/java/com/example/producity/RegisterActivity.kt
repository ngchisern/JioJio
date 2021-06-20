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
        val username = findViewById<EditText>(R.id.display_name).text.toString()
        val email = findViewById<EditText>(R.id.sign_up_email).text.toString()
        val password = findViewById<EditText>(R.id.sign_up_password).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.confirm_password).text.toString()

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email/pass", Toast.LENGTH_SHORT).show()
            return
        }


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