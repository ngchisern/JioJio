package com.example.producity.models.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.producity.MyFirebase
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val TAG = "ActivityDataSource"

class ActivityRemoteDataSource: IActivityRemoteDataSource {

    private val db by lazy { MyFirebase.db }

    override suspend fun fetchUpcomingActivities(username: String): List<Activity> {
        return try {
            db.collection("activity")
                .whereArrayContains("participant", username)
                .orderBy("date")
                .startAt(Timestamp.now())
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Activity::class.java) }
        } catch (e: Exception) {
            Log.d(TAG, "Error getting upcoming activities: $e")
            listOf()
        }
    }

    override fun fetchPastActivity(username: String): MutableLiveData<List<Activity>> {

        val list = MutableLiveData<List<Activity>>(listOf())

        db.collection("activity")
            .whereArrayContains("participant", username)
            .orderBy("date", Query.Direction.DESCENDING)
            .startAt(Timestamp.now())
            .get()
            .addOnSuccessListener {
                val temp = it.toObjects(Activity::class.java)
                list.value = temp
            }

        return list

    }

    override fun manageActivity(activity: Activity) {
        Firebase.firestore.collection("activity")
            .document(activity.docId)
            .set(activity)
            .addOnSuccessListener {
                Log.d(TAG, "edited activity")
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    override fun removeParticipant(username: String, docId: String) {
        val db = Firebase.firestore

        val delete = hashMapOf<String, Any>(
            "participant" to FieldValue.arrayRemove(username)
        )

        db.document("activity/$docId")
            .update(delete)
            .addOnSuccessListener {
                Log.d("Main", "Removed from firestore")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }
    }

    override fun addParticipant(username: String, docId: String) {
        val union = hashMapOf<String, Any>(
            "participant" to FieldValue.arrayUnion(username)
        )

        db.document("activity/$docId")
            .update(union)
            .addOnSuccessListener {
                Log.d("Main", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }
    }

    override suspend fun getNextActivity(username: String): Activity {

        return try {
            val activity = db.collection("activity")
                .whereArrayContains("participant", username)
                .orderBy("date")
                .startAt(Timestamp.now())
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Activity::class.java) }

            activity[0]
        } catch (e: Exception) {
            Activity()
        }
    }


}