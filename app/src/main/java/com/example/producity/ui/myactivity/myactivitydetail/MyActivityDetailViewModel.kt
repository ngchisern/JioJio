package com.example.producity.ui.myactivity.myactivitydetail

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.example.producity.models.User
import com.example.producity.models.source.ActivityRepository
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
            .limitToFirst(6)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Participant>()
                    snapshot.children.forEach {
                        rtdb.getReference("participant/${it.value.toString()}")
                            .get()
                            .addOnSuccessListener { x ->
                                val temp = x.getValue(Participant::class.java)  ?: return@addOnSuccessListener
                                list.add(temp)
                                participantList.value = list
                            }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })
    }

    fun removeParticipant(username: String) {
        activityRepository.removeParticipant(username, currentActivity!!.docId)
        participantRepository.removeFromDatabase(username, currentActivity!!.docId)
    }

    fun assignNewOwner() {
        val db = Firebase.firestore

        db.document("activity/${currentActivity!!.docId}")
            .get()
            .addOnSuccessListener {
                val act = it.toObject(Activity::class.java) ?: return@addOnSuccessListener
                val replace = act.participant.firstOrNull { x -> x != act.owner }

                if(replace == null) {
                    deleteActivity()
                } else {
                    db.document("activity/${currentActivity!!.docId}")
                        .update("owner", replace)

                    val rtdb = Firebase.database

                    rtdb.getReference("activity/${currentActivity!!.docId}/participant")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(item in snapshot.children) {
                                    if(item.getValue(String::class.java) == replace) {
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

    private fun deleteActivity() {
        val db = Firebase.firestore
        val rtdb = Firebase.database

        db.document("activity/${currentActivity!!.docId}")
            .delete()

        rtdb.getReference("activity/${currentActivity!!.docId}")
            .removeValue()

        rtdb.getReference("chatroom/${currentActivity!!.docId}")
            .removeValue()


    }

    fun updateActivity(activity: Activity) {
        activityRepository.manageActivity(activity)
    }

    fun loadUserImage(username: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("profile_pictures/${username}")
            .downloadUrl
            .addOnSuccessListener {  uri ->
                Picasso.get().load(uri).into(view)
            }
    }

    fun loadActivityImage(docId: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("activity_images/${docId}")
            .downloadUrl
            .addOnSuccessListener {  uri ->
                Picasso.get().load(uri).into(view)
            }
    }

    fun loadNickname(username: String, view: TextView) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/$username")
            .get()
            .addOnSuccessListener {
                if(!it.exists()) return@addOnSuccessListener

                view.text = it.getValue(Participant::class.java)!!.nickname
            }
    }

    fun addParticipant(user: User, docId: String) {
        activityRepository.addParticipant(user.username, docId)
        participantRepository.addToDataBase(user, docId)
    }

}

@Suppress("UNCHECKED_CAST")
class MyActivityDetailViewModelFactory(
    private val participantRepo: IParticipantRepository,
    private val activityRepo: IActivityRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (MyActivityDetailViewModel(participantRepo, activityRepo) as T)
}