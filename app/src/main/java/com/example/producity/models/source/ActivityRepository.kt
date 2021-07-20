package com.example.producity.models.source

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.Activity
import com.example.producity.models.source.remote.IActivityRemoteDataSource

class ActivityRepository(
    private val activityRemoteDataSource: IActivityRemoteDataSource
) : IActivityRepository {

    private var upcomingActivity: List<Activity>? = null
    private var pastActivity: List<Activity>? = null

    override fun fetchUserActivity(username: String) {
//        upcomingActivity = activityRemoteDataSource.fetchUpcomingActivities(username)
//        pastActivity = activityRemoteDataSource.fetchPastActivity(username)
    }

    override fun getUserActivity(position: Int, isUpcoming: Boolean): Activity {
        return if (isUpcoming) {
            upcomingActivity!![position]
        } else {
            pastActivity!![position]
        }
    }

    override suspend fun getUpcomingActivities(username: String): List<Activity> {
        return activityRemoteDataSource.fetchUpcomingActivities(username)
    }

    override fun getPastActivities(username: String): MutableLiveData<List<Activity>> {
        return activityRemoteDataSource.fetchPastActivity(username)
    }

    override fun manageActivity(activity: Activity) {
        activityRemoteDataSource.manageActivity(activity)
    }

    override fun removeParticipant(username: String, docId: String) {
        activityRemoteDataSource.removeParticipant(username, docId)
    }

    override fun addParticipant(username: String, docId: String) {
        activityRemoteDataSource.addParticipant(username, docId)
    }

    override suspend fun getNextActivity(username: String): Activity {
        return activityRemoteDataSource.getNextActivity(username)
    }


}