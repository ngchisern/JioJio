package com.example.producity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

private const val RC_SIGN_IN = 1

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(ServiceLocator.provideAuthRepository())
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val logInButton: Button = findViewById(R.id.login_button)
        val goToSignUp: TextView = findViewById(R.id.sign_up_text)

        logInButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.login_email).text.toString()
            val password = findViewById<EditText>(R.id.login_password).text.toString()

            val isSuccessful = loginViewModel.logIn(email, password)

            if (isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                findViewById<TextView>(R.id.login_error_message).text = "Wrong email or password"
            }

            /* TODO Uncomment this and remove the above if-else block to check for email verification
            if (!isSuccessful) {
                findViewById<TextView>(R.id.login_error_message).setText("Wrong email or password")
            } else if (!auth.currentUser!!.isEmailVerified) {
                findViewById<TextView>(R.id.login_error_message).setText(getString(R.string.email_verification_text))
                auth.signOut()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
             */
        }

        goToSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInButton: SignInButton = findViewById(R.id.google_sign_in_button)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent();
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Timber.d("firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.d("Google sign in failed")
                findViewById<TextView>(R.id.login_error_message).text =
                    "An error occured. Please try again."
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")

                    if (task.result.additionalUserInfo!!.isNewUser) {
                        Timber.d("New Google account")
                        navigateToGoogleLoginRegisterUsernameActivity()
                    } else {
                        val currentUid = auth.currentUser!!.uid
                        db.collection("users")
                            .whereEqualTo("uid", currentUid)
                            .limit(1)
                            .get()
                            .addOnSuccessListener {
                                if (it.isEmpty) {
                                    Timber.d("Google account not yet registered")
                                    navigateToGoogleLoginRegisterUsernameActivity()
                                } else {
                                    navigateToMainActivity()
                                }
                            }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.d(task.exception, "signInWithCredential:failure")
                    findViewById<TextView>(R.id.login_error_message).text =
                        "An error occured. Please try again."
                }
            }
    }

    private fun navigateToGoogleLoginRegisterUsernameActivity() {
        val intent = Intent(this, GoogleLoginRegisterUsernameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}