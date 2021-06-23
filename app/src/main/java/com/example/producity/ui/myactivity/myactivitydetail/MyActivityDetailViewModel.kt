package com.example.producity.ui.myactivity.myactivitydetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.ActivityLog
import com.example.producity.models.Participant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyActivityDetailViewModel : ViewModel() {

    private val rtdb = Firebase.database

    val participantList: MutableLiveData<List<Participant>> = MutableLiveData(listOf())

    fun updateList(documentId: String) {

        rtdb.getReference().child("participant/$documentId")
            .limitToFirst(6)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Participant>()

                    snapshot.children.forEach {
                        val temp = it.getValue(Participant::class.java) ?: return
                        list.add(temp)
                    }
                    Log.d("Main", list.size.toString())

                    participantList.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })
    }

}