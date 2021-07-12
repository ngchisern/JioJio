package com.example.producity.ui.myactivity.myactivitydetail.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.ChatRoom
import com.example.producity.models.Message
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatViewModel: ViewModel() {

    private val rtdb = Firebase.database

    var documentId: String = ""
    val chatMessages: MutableLiveData<List<Message>> = MutableLiveData(listOf())

    fun updateList() {
        rtdb.getReference().child("activity/$documentId/message")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach {
                        val temp = it.getValue(Message::class.java) ?: return
                        list.add(temp)
                    }
                    Log.d("Main", list.size.toString())
                    chatMessages.value = list

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })
    }

    fun sendMessage(message: Message) {
        rtdb.getReference("activity/$documentId/message").push()
            .setValue(message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "added noti to database")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }

        rtdb.getReference("chatroom/$documentId")
            .get()
            .addOnSuccessListener {
                val chatRoom = it.getValue(ChatRoom::class.java)!!

                val unread = chatRoom.unread.mapValues {
                    it.value + 1
                }

                val updated = ChatRoom(message.timestamp, chatRoom.group, chatRoom.docId,
                                        message.username, message.message, unread )

                rtdb.getReference("chatroom/$documentId")
                    .setValue(updated)
                    .addOnSuccessListener {
                        Log.d("ChatViewModel", "updated chatroom")
                    }

            }
    }

    fun updateReadMessage(username: String) {
        val rtdb = Firebase.database

        rtdb.getReference("chatroom/$documentId/unread/$username")
            .setValue(0)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "read messages")
            }
    }

}