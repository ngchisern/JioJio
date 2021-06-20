package com.example.producity.ui.myactivity.myactivitydetail.log

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.ActivityLog
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyActivityLogViewModel: ViewModel() {
    private val rtdb = Firebase.database

    val activityLog: MutableLiveData<List<ActivityLog>> = MutableLiveData(listOf())

    fun updateList(documentId: String) {

        rtdb.getReference().child("activitylog/$documentId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ActivityLog>()
                    snapshot.children.forEach {
                        val temp = it.getValue(ActivityLog::class.java) ?: return
                        list.add(temp)
                    }
                    Log.d("Main", list.size.toString())
                    activityLog.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })
    }

}