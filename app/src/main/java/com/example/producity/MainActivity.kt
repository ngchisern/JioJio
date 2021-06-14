package com.example.producity

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.producity.models.Friend
import com.example.producity.models.User
import com.example.producity.ui.explore.ExploreListItem
import com.example.producity.ui.explore.ExploreViewModel
import com.example.producity.ui.friends.FriendListItem
import com.example.producity.ui.friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityListItem
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.example.producity.ui.profile.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.sql.Time
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)

            val bitmapDrawable = BitmapDrawable(bitmap)

            val imageButton: ImageButton =
                dialogView?.findViewById(R.id.add_photo_image_button) ?: return

            Log.d(TAG, "${imageButton.background}")
            imageButton.setBackgroundDrawable(bitmapDrawable)

            Log.d(TAG, "${imageButton.background}")
        }
    }


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
                            val list: MutableList<FriendListItem> = mutableListOf()
                            it.forEach { doc ->
                                val friend = doc.toObject(Friend::class.java)
                                list.add(FriendListItem(friend.username))
                            }
                            friendListViewModel.updateFriendList(list)
                        }

                    db.collection("activity")
                        .whereEqualTo("owner", username)
                        .get()
                        .addOnSuccessListener {
                            Log.d(TAG, "updated activities")
                            val list: MutableList<MyActivityListItem> = mutableListOf()
                            it.forEach { doc ->
                                val activity = doc.toObject(Activity::class.java)
                                list.add(
                                    MyActivityListItem(
                                        activity.imageUrl, activity.title,
                                        activity.time, activity.pax
                                    )
                                )
                            }
                            myActivityViewModel.updateList(list)
                        }
                        .addOnFailureListener {
                            Log.d(TAG, it.message.toString())
                        }

                    db.collection("activity")
                        .whereArrayContains("viewers", username)
                        .get()
                        .addOnSuccessListener {
                            Log.d(TAG, "explore ${it.size()} activities")
                            Log.d(TAG, "updated explore activities")
                            val list: MutableList<ExploreListItem> = mutableListOf()
                            it.forEach { doc ->
                                val activity = doc.toObject(Activity::class.java)
                                list.add(
                                    ExploreListItem(
                                        activity.imageUrl, activity.title,
                                        activity.time, activity.pax
                                    )
                                )
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
        val builder = MaterialAlertDialogBuilder(this)

        val input = EditText(this)
        input.setHint("Enter Activity")
        input.inputType = InputType.TYPE_CLASS_TEXT

        val view: View = LayoutInflater.from(this).inflate(R.layout.add_schedule, null)
        dialogView = view

        builder.setView(view)

        val imageButton: ImageButton = view.findViewById(R.id.add_photo_image_button)

        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        val pickTime: ImageButton = view.findViewById(R.id.choose_time)

        pickTime.setOnClickListener {
            showTimeDialog()
        }

        builder.setPositiveButton("Done", DialogInterface.OnClickListener { dialog, which ->
            uploadImageForFirebaseStorage()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun showTimeDialog() {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    var stage = "a.m."

                    if (hourOfDay >= 12) stage = "p.m."

                    val text = "${hourOfDay % 12} : $minute $stage"

                    dialogView?.findViewById<EditText>(R.id.timeInput)
                        ?.setText(text)
                }
            }, hour, minute, false
        )

        mTimePicker.show()
    }


    private fun uploadImageForFirebaseStorage() {

        if (selectedPhoto == null) {
            addToDataBase("https://www.enjpg.com/img/2020/cute-2.png")
            return
        }

        val storage = Firebase.storage
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/activity_images/$filename")

        ref.putFile(selectedPhoto!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "FileLocation: $it")
                    addToDataBase(it.toString())
                }
            }

    }

    private fun addToDataBase(imageUrl: String) {
        val text1: EditText = dialogView!!.findViewById(R.id.titleInput)
        val text2: EditText = dialogView!!.findViewById(R.id.timeInput)
        val text3: EditText = dialogView!!.findViewById(R.id.paxInput)

        val user = friendListViewModel.currentUser

        val listOfFriends: MutableList<String> = mutableListOf()
        val src = friendListViewModel.allFriends.value ?: listOf()

        for (elem in src) {
            listOfFriends.add(elem.username)
        }


        val newActivity = Activity(
            imageUrl,
            text1.text.toString(),
            user.value?.username.toString(),
            text2.text.toString(),
            text3.text.toString().toInt(),
            listOfFriends
        )

        db.collection("activity")
            .add(newActivity)
            .addOnSuccessListener {
                Log.d(TAG, "added a new activity")

                myActivityViewModel.addActivity(
                    MyActivityListItem(
                        imageUrl,
                        newActivity.title,
                        newActivity.time,
                        newActivity.pax
                    )
                )
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

}