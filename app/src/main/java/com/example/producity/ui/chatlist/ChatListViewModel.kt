package com.example.producity.ui.chatlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.Activity
import com.example.producity.models.ChatRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ChatListViewModel : ViewModel() {

    private val rtdb = Firebase.database
    val chatRooms: MutableLiveData<List<ChatRoom>> = MutableLiveData(listOf())

    fun updateList(list: List<Activity>) {
        var updated: MutableList<ChatRoom> = mutableListOf()

        list.forEach { it ->
            rtdb.getReference("chatroom/${it.docId}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val temp = snapshot.getValue(ChatRoom::class.java) ?: return

                        updated = updated.filter { x -> x.docId != temp.docId }.toMutableList()

                        updated.add(temp)
                        chatRooms.value = updated.sortedByDescending { it.timestamp }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.d("failed to update log")
                    }
                })
        }
    }


}