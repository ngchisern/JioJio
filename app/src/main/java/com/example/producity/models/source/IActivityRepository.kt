package com.example.producity.models.source

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.Activity

interface IActivityRepository {
    fun fetchUserActivity(username: String)
    fun getUserActivity(position: Int, isUpcoming: Boolean): Activity
    fun manageActivity(activity: Activity)
    fun removeParticipant(username: String, docId: String)
    fun addParticipant(username: String, docId: String)
    suspend fun getUpcomingActivities(username: String): List<Activity>
    fun getPastActivities(username: String): MutableLiveData<List<Activity>>
    suspend fun getNextActivity(username: String): Activity
}