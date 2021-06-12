package com.example.producity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.producity.databinding.ActivityMainBinding
import com.example.producity.models.FriendSnippet
import com.example.producity.models.User
import com.example.producity.ui.friends.FriendListItem
import com.example.producity.ui.friends.FriendListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "Main"
    }

    private lateinit var auth: FirebaseAuth

    private val friendListViewModel: FriendListViewModel by viewModels()
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
                R.id.navigation_home, R.id.navigation_explore,
                R.id.navigation_friends, R.id.navigation_profile
            )
        )



        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }

        auth = Firebase.auth
        verifyUser()
    }


    private fun verifyUser() {
        val currentUser = auth.currentUser

        if(currentUser == null) {
            quit()
            return
        }

        val db = Firebase.firestore

        db.collection("users")
            .whereEqualTo("uid", currentUser.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if(it == null || it.isEmpty) {
                    Log.d("Main", "cant set up user info")
                    quit()
                    return@addOnSuccessListener
                }

                it.forEach { doc ->
                    val temp = doc.toObject(User::class.java)
                    friendListViewModel.updateUser(temp)
                    Log.d("Main", "Updated")
                    val username = temp.username

                    db.collection("users/$username/friends")
                        .orderBy("username")
                        .get()
                        .addOnSuccessListener {
                            val list: MutableList<FriendListItem> = mutableListOf()
                            it.forEach { doc ->
                                val friend = doc.toObject(FriendSnippet::class.java)
                                list.add(FriendListItem(friend.username))
                            }
                            friendListViewModel.updateFriendList(list)
                        }
                }
            }

    }

    private fun quit() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("New Activity")

        val input = EditText(this)
        input.setHint("Enter Activity")
        input.inputType = InputType.TYPE_CLASS_TEXT

        val view: View = LayoutInflater.from(this).inflate(R.layout.add_schedule, null)

        builder.setView(view)

        builder.setPositiveButton("Done", DialogInterface.OnClickListener { dialog, which ->
            val text1: EditText = view.findViewById(R.id.taskInput1)
            val text2: EditText = view.findViewById(R.id.taskInput2)
            /*
            homeViewModel.insert(
                ScheduleDetail(
                    homeViewModel.targetDate.value!!,
                    text1.text.toString(),
                    text2.text.toString()
                )
            )

             */
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()

    }

}