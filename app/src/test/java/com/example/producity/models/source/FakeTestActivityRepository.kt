package com.example.producity.models.source

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.Activity
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.collections.LinkedHashMap

class FakeTestActivityRepository : IActivityRepository {
    // doc id to activity
    var activities = linkedMapOf<String, Activity>(
        "doc1" to Activity("doc1", "Activity 1", "activity 1", "ben", 0, true, Date(),
            0.0,0.0, 5, "no", listOf("ben"), listOf()),
        "doc2" to Activity("doc2", "Activity 2", "activity 2", "alex", 0, true, Date(),
            0.0,0.0, 5, "yes", listOf("alex"), listOf())
    )

    val time: Long = 1626769233 // now

    fun setUp(map: LinkedHashMap<String, Activity>) {
        activities = map
    }

    override fun manageActivity(activity: Activity) {
        activities[activity.docId] = activity
    }

    override fun removeParticipant(username: String, docId: String) {
        val temp = activities[docId]!!.participant.toMutableList()
        temp.remove(username)
        activities[docId]!!.participant = temp
    }

    override fun addParticipant(username: String, docId: String) {
        val temp = activities[docId]!!.participant.toMutableList()
        temp.add(username)
        activities[docId]!!.participant = temp
    }

    override suspend fun getUpcomingActivities(username: String): List<Activity> {
        return activities.map{x -> x.value}.filter { x -> x.date > Date(time) && x.participant.contains(username) }
    }


    override fun getPastActivities(username: String): MutableLiveData<List<Activity>> {
        return MutableLiveData(activities.map{x -> x.value}.filter { x -> x.date < Date(time) && x.participant.contains(username) })
    }

    override suspend fun getNextActivity(username: String): Activity? {
        return activities.map { x -> x.value }
            .firstOrNull { x -> x.date > Date(time) && x.participant.contains(username) }
    }

}