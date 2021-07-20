package com.example.producity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.producity.databinding.ActivityMainBinding
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.example.producity.ui.chatlist.ChatListViewModel
import com.example.producity.ui.explore.ExploreViewModel
import com.example.producity.ui.explore.ExploreViewModelFactory
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "Main"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val sharedViewModel: SharedViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by viewModels()

    private val chatListViewModel: ChatListViewModel by viewModels()

    private val exploreViewModel: ExploreViewModel by viewModels() {
        ExploreViewModelFactory(ServiceLocator.provideParticipantRepository(), ServiceLocator.provideActivityRepository())
    }

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        auth = Firebase.auth
        db = Firebase.firestore

        verifyUser()
    }

    private fun verifyUser() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            quit()
            return
        }

        // Delete
        Log.d(TAG, "displayName: ${currentUser.displayName}")
        Log.d(TAG, "email: ${currentUser.email}")
        Log.d(TAG, "uid: ${currentUser.uid}")

        db.collection("users")
            .whereEqualTo("uid", currentUser.uid)
            .limit(1)
            .get()
            .addOnSuccessListener { it ->
                if (it == null || it.isEmpty) {
                    Log.d("Main", "cant set up user info")
                    quit()
                    return@addOnSuccessListener
                }

                it.forEach { doc ->
                    val temp = doc.toObject(User::class.java)
                    sharedViewModel.updateUser(temp)
                    val username = temp.username

                    sharedViewModel.loadUserImage()

                    myActivityViewModel.getRecommendation(username)

                    db.collection("users/$username/friends")
                        .orderBy("username")
                        .get()
                        .addOnSuccessListener {
                            val list: MutableList<User> = mutableListOf()
                            it.forEach { doc ->
                                val friend = doc.toObject(User::class.java)
                                list.add(friend)
                            }
//                            friendListViewModel.updateFriendList(list)
                        }


                    db.collection("activity")
                        .whereArrayContains("participant", username)
                        .orderBy("date")
                        .startAt(Timestamp.now())
                        .limit(10)
                        .addSnapshotListener { value, error ->
                            if(error != null) {
                                return@addSnapshotListener
                            }
                            val list = value?.toObjects(Activity::class.java) ?: return@addSnapshotListener
                            myActivityViewModel.updateList(list)
                            chatListViewModel.updateList(list)
                        }

                    db.collection("activity")
                        .orderBy("date")
                        .startAt(Timestamp.now())
                        .limit(5)
                        .get()
                        .addOnSuccessListener { x ->
                            val list = x.toObjects(Activity::class.java)

                            if(list.isEmpty()) {
                                return@addOnSuccessListener
                            }

                            exploreViewModel.latest = list.last().date

                            val result = list.filter { y -> !y.participant.contains(username) }.toMutableList()
                            exploreViewModel.updateList(result)
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


}