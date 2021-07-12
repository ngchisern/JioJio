package com.example.producity.ui.myactivity.myactivitydetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.example.producity.models.source.ActivityRepository
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IParticipantRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

        rtdb.getReference().child("activity/$documentId/participant")
            .limitToFirst(6)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Participant>()
                    snapshot.children.forEach {
                        rtdb.getReference("participant/${it.value.toString()}")
                            .get()
                            .addOnSuccessListener {
                                val temp = it.getValue(Participant::class.java)  ?: return@addOnSuccessListener
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

    fun addActivity(user: Participant, docId: String) {
        activityRepository.addParticipant(user.username, docId)
        participantRepository.addToDataBase(user, docId)

    }

    fun updateActivity(activity: Activity) {
        activityRepository.manageActivity(activity)
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