package com.example.producity.models.source

import com.example.producity.models.Activity
import com.example.producity.models.source.remote.ActivityRemoteDataSource
import com.example.producity.models.source.remote.IActivityRemoteDataSource

class ActivityRepository(
    private val activityRemoteDataSource: IActivityRemoteDataSource
        ): IActivityRepository {

    private var upcomingActivity: List<Activity>? = null
    private var pastActivity: List<Activity>? = null

    override fun fetchUserActivity(username: String) {
        upcomingActivity = activityRemoteDataSource.fetchUpcomingActivity(username)
        pastActivity = activityRemoteDataSource.fetchPastActivity(username)
    }

    override fun getUserActivity(position: Int, isUpcoming: Boolean): Activity {
        if(isUpcoming) {
            return upcomingActivity!![position]
        } else {
            return pastActivity!![position]
        }
    }

}