package com.example.producity.ui.myactivity.myactivitydetail

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.*
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IParticipantRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import timber.log.Timber

class MyActivityDetailViewModel(
    private val participantRepository: IParticipantRepository,
    private val activityRepository: IActivityRepository
) : ViewModel() {

    var currentActivity: Activity? = null

    val participantList: MutableLiveData<List<Participant>> = MutableLiveData(listOf())

    fun setActivity(activity: Activity) {
        currentActivity = activity
    }

    fun updateList(documentId: String) {
        val rtdb = Firebase.database

        rtdb.reference.child("activity/$documentId/participant")
            .limitToFirst(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Participant>()
                    participantList.value = list
                    snapshot.children.forEach {
                        rtdb.getReference("participant/${it.value.toString()}")
                            .get()
                            .addOnSuccessListener { x ->
                                val temp = x.getValue(Participant::class.java)
                                    ?: return@addOnSuccessListener
                                Timber.d(temp.username)
                                list.add(temp)

                                participantList.value = list
                            }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.d("failed to update log")
                }
            })

    }

    fun removeParticipant(username: String) {
        activityRepository.removeParticipant(username, currentActivity!!.docId)
        participantRepository.removeFromDatabase(username, currentActivity!!.docId)
    }

    fun assignNewOwner() {
        val db = Firebase.firestore
        val tracker = currentActivity!!

        db.document("activity/${tracker.docId}")
            .get()
            .addOnSuccessListener {
                val act = it.toObject(Activity::class.java) ?: return@addOnSuccessListener
                val replace = act.participant.firstOrNull { x -> x != act.owner }

                if (replace == null) {
                    deleteActivity(tracker)
                } else {
                    db.document("activity/${currentActivity!!.docId}")
                        .update("owner", replace)

                    val rtdb = Firebase.database

                    rtdb.getReference("activity/${currentActivity!!.docId}/participant")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (item in snapshot.children) {
                                    if (item.getValue(String::class.java) == replace) {
                                        item.ref.removeValue()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }


                        })

                }
            }
    }

    private fun deleteActivity(activity: Activity) {
        val db = Firebase.firestore
        val rtdb = Firebase.database
        val storage = Firebase.storage

        db.document("activity/${activity.docId}")
            .delete()

        rtdb.getReference("activity/${activity.docId}")
            .removeValue()

        rtdb.getReference("chatroom/${activity.docId}")
            .removeValue()

        storage.reference.child("/activity_images/${activity.docId}")
            .delete()

    }

    fun updateActivity(activity: Activity) {
        activityRepository.manageActivity(activity)
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

    fun addParticipant(user: User, docId: String) {
        activityRepository.addParticipant(user.username, docId)
        participantRepository.addToDataBase(user, docId)
    }

    fun sendMessage(message: Message) {
        val rtdb = Firebase.database
        val docid = currentActivity!!.docId

        rtdb.getReference("activity/$docid/message").push()
            .setValue(message)
            .addOnSuccessListener {
                Timber.d("added noti to database")
            }
            .addOnFailureListener {
                Timber.d(it.message.toString())
            }

        rtdb.getReference("chatroom/$docid")
            .get()
            .addOnSuccessListener {
                val chatRoom = it.getValue(ChatRoom::class.java) ?: return@addOnSuccessListener

                val unread = chatRoom.unread.mapValues { x ->
                    x.value + 1
                }

                val updated = ChatRoom(
                    message.timestamp, chatRoom.group, chatRoom.docId,
                    message.username, message.message, unread
                )

                rtdb.getReference("chatroom/$docid")
                    .setValue(updated)
                    .addOnSuccessListener {
                        Timber.d("updated chatroom")
                    }

            }
    }



}

@Suppress("UNCHECKED_CAST")
class MyActivityDetailViewModelFactory(
    private val participantRepo: IParticipantRepository,
    private val activityRepo: IActivityRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (MyActivityDetailViewModel(participantRepo, activityRepo) as T)
}