package com.example.producity.ui.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.Notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationViewModel: ViewModel() {
    private val rtdb = Firebase.database

    val notificationList: MutableLiveData<List<Notification>> = MutableLiveData(listOf())
    var docId: List<String> = listOf()

    fun updateList(username: String) {

        rtdb.getReference().child("notification/$username")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Notification>()
                    val docList = mutableListOf<String>()
                    snapshot.children.forEach {
                        val temp = it.getValue(Notification::class.java) ?: return
                        list.add(temp)
                        docList.add(it.key!!)
                    }
                    Log.d("Main", list.size.toString())
                    notificationList.value = list.reversed()
                    docId = docList.reversed()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })

    }




}