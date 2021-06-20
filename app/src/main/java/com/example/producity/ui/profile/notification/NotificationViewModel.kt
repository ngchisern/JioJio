package com.example.producity.ui.profile.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.ActivityLog
import com.example.producity.models.Notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationViewModel: ViewModel() {
    private val rtdb = Firebase.database

    val notificationList: MutableLiveData<List<Notification>> = MutableLiveData(listOf())

    fun updateList(username: String) {

        rtdb.getReference().child("notification/$username")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Notification>()
                    snapshot.children.forEach {
                        val temp = it.getValue(Notification::class.java) ?: return
                        list.add(temp)
                    }
                    Log.d("Main", list.size.toString())
                    notificationList.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })


    }

}