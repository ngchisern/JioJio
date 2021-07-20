package com.example.producity.ui.createactivity

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.producity.models.Activity
import com.example.producity.models.ChatRoom
import com.example.producity.models.Message
import com.google.firebase.Timestamp
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import timber.log.Timber
import java.util.*

class CreateActivityViewModel : ViewModel() {
    var title: String? = null
    var owner: String? = null
    private var privacy: Int? = null
    private var isVirtual: Boolean? = null
    var date: Date? = null
    var lat: Double? = null
    var long: Double? = null
    var pax: Int? = null
    var description: String? = null
    var participant: String? = null

    fun updateMode(isVirtual: Boolean) {
        this.isVirtual = isVirtual
    }

    fun updateLocation(lat: Double, long: Double) {
        this.lat = lat
        this.long = long
    }

    fun updatePrivacy(privacy: Int) {
        this.privacy = privacy
    }

    fun createActivity(
        photo: Uri,
        title: String,
        creator: String,
        description: String,
        date: Date,
        pax: Int,
        image: InputImage
    ) {
        val db = Firebase.firestore

        val ref = db.collection("activity").document()

        val storage = Firebase.storage
        val filename = storage.getReference("/activity_images/${ref.id}")

        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.4f)
            .build()

        val labeler = ImageLabeling.getClient(options)

        labeler.process(image)
            .addOnSuccessListener {
                val total = if (it.size > 5) 5 else it.size
                val labels = it.sortedByDescending { x -> x.confidence }
                    .subList(0, total)
                    .map { label -> label.text }


                val rtdb = Firebase.database

                filename.putFile(photo)
                    .addOnSuccessListener {
                        filename.downloadUrl.addOnSuccessListener {

                            val newActivity = Activity(
                                ref.id,
                                title,
                                title.toLowerCase(Locale.ROOT),
                                creator,
                                privacy!!,
                                isVirtual!!,
                                date,
                                lat!!,
                                long!!,
                                pax,
                                description,
                                listOf(creator),
                                labels
                            )

                            cleanUp()

                            ref.set(newActivity)
                                .addOnSuccessListener {
                                    Timber.d("added a new activity")
                                }
                                .addOnFailureListener { e ->
                                    Timber.d(e.toString())
                                }

                            val chatRoom = ChatRoom(
                                Timestamp.now().toDate().time, title, ref.id, creator,
                                "Hi, this is the chat room for my activity!", mapOf(creator to 0)
                            )

                            rtdb.getReference("chatroom/${ref.id}")
                                .setValue(chatRoom)
                                .addOnSuccessListener {
                                    Timber.d("created chatroom")
                                }

                            val message = Message(
                                "Hi, this is the chat room for my activity!",
                                creator, Timestamp.now().toDate().time
                            )

                            rtdb.getReference("activity/${ref.id}/message").push()
                                .setValue(message)
                                .addOnSuccessListener {
                                    Timber.d("added noti to database")
                                }
                                .addOnFailureListener { e ->
                                    Timber.d(e.message.toString())
                                }


                        }
                    }

                for (item in labels) {
                    rtdb.reference.child("participant/$creator/recommendation/$item")
                        .setValue(ServerValue.increment(1))
                }

            }

    }

    fun cleanUp() {
        title = null
        owner = null
        privacy = null
        isVirtual = null
        date = null
        lat = null
        long = null
        pax = null
        description = null
        participant = null
    }

}