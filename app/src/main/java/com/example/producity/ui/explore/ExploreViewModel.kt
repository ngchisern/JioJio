package com.example.producity.ui.explore

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.example.producity.models.User
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IParticipantRepository
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class ExploreViewModel(
    private val participantRepository: IParticipantRepository,
    private val activityRepository: IActivityRepository
) : ViewModel() {

    val exploreActivities: MutableLiveData<MutableList<Activity>> = MutableLiveData(mutableListOf())
    var allActivities: MutableList<Activity> = mutableListOf()

    var latest: Date? = null

    fun getList(): List<Activity> {
        return exploreActivities.value!!
    }

    fun updateList(newList: MutableList<Activity>) {
        allActivities = newList
        exploreActivities.value = newList
    }

    fun addParticipant(user: User, docId: String) {
        activityRepository.addParticipant(user.username, docId)
        participantRepository.addToDataBase(user, docId)
    }

    fun search(query: String) {
        val db = Firebase.firestore

        db.collection("activity")
            .orderBy("lowerCaseTitle")
            .startAt(query.toLowerCase(Locale.ROOT))
            .endAt(query.toLowerCase(Locale.ROOT) + "\uf8ff")
            .limit(20)
            .get()
            .addOnSuccessListener {
                val list = it.toObjects(Activity::class.java)
                exploreActivities.value = list
            }
    }

    fun getAll() {
        exploreActivities.value = allActivities
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

@Suppress("UNCHECKED_CAST")
class ExploreViewModelFactory(
    private val participantRepo: IParticipantRepository,
    private val activityRepo: IActivityRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ExploreViewModel(participantRepo, activityRepo) as T)
}

