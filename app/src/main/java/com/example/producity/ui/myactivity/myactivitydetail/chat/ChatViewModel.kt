package com.example.producity.ui.myactivity.myactivitydetail.chat

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.ChatRoom
import com.example.producity.models.Message
import com.example.producity.models.Participant
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import timber.log.Timber

class ChatViewModel : ViewModel() {

    private val rtdb = Firebase.database

    var documentId: String = ""
    val chatMessages: MutableLiveData<List<Message>> = MutableLiveData(listOf())

    fun updateList() {
        rtdb.reference.child("activity/$documentId/message")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach {
                        val temp = it.getValue(Message::class.java) ?: return
                        list.add(temp)
                    }
                    Timber.d(list.size.toString())
                    chatMessages.value = list

                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.d("failed to update log")
                }
            })
    }

    fun sendMessage(message: Message) {
        rtdb.getReference("activity/$documentId/message").push()
            .setValue(message)
            .addOnSuccessListener {
                Timber.d("added noti to database")
            }
            .addOnFailureListener {
                Timber.d(it.message.toString())
            }

        rtdb.getReference("chatroom/$documentId")
            .get()
            .addOnSuccessListener {
                val chatRoom = it.getValue(ChatRoom::class.java)!!

                val unread = chatRoom.unread.mapValues { x ->
                    x.value + 1
                }

                val updated = ChatRoom(
                    message.timestamp, chatRoom.group, chatRoom.docId,
                    message.username, message.message, unread
                )

                rtdb.getReference("chatroom/$documentId")
                    .setValue(updated)
                    .addOnSuccessListener {
                        Timber.d("updated chatroom")
                    }

            }
    }


    fun updateReadMessage(username: String) {
        val rtdb = Firebase.database

        rtdb.getReference("chatroom/$documentId/unread/$username")
            .setValue(0)
            .addOnSuccessListener {
                Timber.d("read messages")
            }
    }

    fun loadUserImage(username: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("profile_pictures/${username}")
            .downloadUrl
            .addOnSuccessListener { uri ->

                Picasso.get().load(uri).into(view)
            }
    }

    fun loadActivityImage(docId: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("activity_images/${docId}")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(view)
            }
    }

    fun loadNickname(username: String, view: TextView) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/$username")
            .get()
            .addOnSuccessListener {
                if (!it.exists()) return@addOnSuccessListener

                view.text = it.getValue(Participant::class.java)!!.nickname
            }
    }

}