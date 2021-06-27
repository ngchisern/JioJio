package com.example.producity.models.source.remote

import android.util.Log
import com.example.producity.MyFirebase
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "ActivityDataSource"

object ActivityRemoteDataSource: IActivityRemoteDataSource {

    private val db by lazy { MyFirebase.db }

    // change - listener not useful here
    override fun fetchUpcomingActivity(username: String): List<Activity> {
        var list: List<Activity> = listOf()

        val promise = db.collection("activity")
            .whereArrayContains("participant", username)
            .orderBy("date")
            .startAt(Timestamp.now())
            .limit(50)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    return@addSnapshotListener
                }
                list = value?.toObjects(Activity::class.java) ?: return@addSnapshotListener
            }
        promise.run {
            return list
        }
    }

    override fun fetchPastActivity(username: String): List<Activity> {
        var list: List<Activity> = listOf()

        val promise = db.collection("activity")
            .whereArrayContains("participant", username)
            .orderBy("date", Query.Direction.DESCENDING)
            .startAt(Timestamp.now())
            .limit(15)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    return@addSnapshotListener
                }
                list = value?.toObjects(Activity::class.java) ?: return@addSnapshotListener
            }

        promise.run {
            return list
        }

    }



    override fun manageActivity(activity: Activity) {
        MyFirebase.db.collection("activity")
            .document(activity.docId)
            .set(activity)
            .addOnSuccessListener {
                Log.d(TAG, "edited activity")
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }


}