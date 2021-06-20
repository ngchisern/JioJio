package com.example.producity

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.producity.databinding.ActivityMainBinding
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.example.producity.ui.explore.ExploreViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityFragmentDirections
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.example.producity.ui.profile.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "Main"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val friendListViewModel: FriendListViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val exploreViewModel: ExploreViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_myActivity, R.id.navigation_explore,
                R.id.navigation_friends, R.id.navigation_profile
            )
        )

        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }

        auth = Firebase.auth
        db = Firebase.firestore

        verifyUser()
    }

    var dialogView: View? = null
    var selectedPhoto: Uri? = null



    private fun verifyUser() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            quit()
            return
        }

        db.collection("users")
            .whereEqualTo("uid", currentUser.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if (it == null || it.isEmpty) {
                    Log.d("Main", "cant set up user info")
                    quit()
                    return@addOnSuccessListener
                }

                it.forEach { doc ->
                    val temp = doc.toObject(User::class.java)
                    friendListViewModel.updateUser(temp)
                    Log.d("Main", "updated username")
                    val username = temp.username

                    profileViewModel.updateUserProfile(temp)

                    db.collection("users/$username/friends")
                        .orderBy("username")
                        .get()
                        .addOnSuccessListener {
                            val list: MutableList<User> = mutableListOf()
                            it.forEach { doc ->
                                val friend = doc.toObject(User::class.java)
                                list.add(friend)
                            }
                            friendListViewModel.updateFriendList(list)
                        }

                    db.collection("activity")
                        .whereArrayContains("participant", username)
                        .orderBy("date")
                        .startAt(Timestamp.now())
                        .limit(15)
                        .get()
                        .addOnSuccessListener {
                            Log.d(TAG, "updated activities")
                            val list = it.toObjects(Activity::class.java)
                            myActivityViewModel.updateList(list)
                            it.documents.forEach{
                                myActivityViewModel.documentIds.add(it.id)
                            }
                        }
                        .addOnFailureListener {
                            Log.d(TAG, it.message.toString())
                        }

                    db.collection("activity")
                        .whereArrayContains("participant", username)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .startAt(Timestamp.now())
                        .limit(15)
                        .get()
                        .addOnSuccessListener {
                            Log.d(TAG, "updated ${it.size()} activities")
                            val list = it.toObjects(Activity::class.java)
                            myActivityViewModel.updatePastList(list)
                            it.documents.forEach{
                                myActivityViewModel.pastDocumentIds.add(it.id)
                            }
                        }
                        .addOnFailureListener {
                            Log.d(TAG, it.message.toString())
                        }

                    db.collection("activity")
                        .whereArrayContains("viewers", username)
                        .orderBy("date")
                        .get()
                        .addOnSuccessListener {
                            Log.d(TAG, "updated explore activities")
                            val list = it.toObjects(Activity::class.java)
                            it.documents.forEach {
                                exploreViewModel.documentIds.add(it.id)
                            }
                            exploreViewModel.updateList(list)
                        }
                        .addOnFailureListener {
                            Log.d(TAG, it.message.toString())
                        }

                }
            }

    }

    private fun quit() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showDialog() {
        val action = MyActivityFragmentDirections.actionNavigationMyActivityToAddActivityFragment()
        findNavController(R.id.nav_host_fragment_activity_main).navigate(action)
    }




}