package com.example.producity.models.source.remote

import com.example.producity.models.Activity

interface IActivityRemoteDataSource {
    suspend fun fetchUpcomingActivities(username: String): List<Activity>
    fun fetchPastActivity(username: String): List<Activity>
    fun manageActivity(activity: Activity)

}