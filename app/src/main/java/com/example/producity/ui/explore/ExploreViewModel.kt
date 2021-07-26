package com.example.producity.ui.explore

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import com.example.producity.models.*
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IParticipantRepository
import com.google.firebase.Timestamp
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import timber.log.Timber
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

    fun setUp(username: String) {
        val db = Firebase.firestore
        db.collection("activity")
            .whereEqualTo("privacy", Activity.PUBLIC)
            .orderBy("date")
            .startAt(Timestamp.now())
            .limit(5)
            .get()
            .addOnSuccessListener { x ->
                val list = x.toObjects(Activity::class.java)

                if (list.isEmpty()) {
                    return@addOnSuccessListener
                }

                latest = list.last().date

                val result = list.filter { y -> !y.participant.contains(username) }
                    .toMutableList()
                updateList(result)
            }
            .addOnFailureListener {
                Timber.d(it.message.toString())
            }
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
            .whereEqualTo("privacy", Activity.PUBLIC)
            .orderBy("lowerCaseTitle")
            .startAt(query.toLowerCase(Locale.ROOT))
            .endAt(query.toLowerCase(Locale.ROOT) + "\uf8ff")
            .limit(10)
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

    fun addRecommendation(labels: List<String>, username: String) {
        val rtdb = Firebase.database

        for (item in labels) {
            rtdb.reference.child("participant/$username/recommendation/$item")
                .setValue(ServerValue.increment(1))
        }
    }

    fun sendMessage(message: Message, docId: String) {
        val rtdb = Firebase.database

        rtdb.getReference("activity/$docId/message").push()
            .setValue(message)
            .addOnSuccessListener {
                Timber.d("added noti to database")
            }
            .addOnFailureListener {
                Timber.d(it.message.toString())
            }

        rtdb.getReference("chatroom/$docId")
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

                rtdb.getReference("chatroom/$docId")
                    .setValue(updated)
                    .addOnSuccessListener {
                        Timber.d("updated chatroom")
                    }

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

