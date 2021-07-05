package com.example.producity.models.source

import com.example.producity.models.Activity

interface IActivityRepository {
    fun fetchUserActivity(username: String)
    fun getUserActivity(position: Int, isUpcoming: Boolean): Activity
    suspend fun getUpcomingActivities(username: String): List<Activity>
}