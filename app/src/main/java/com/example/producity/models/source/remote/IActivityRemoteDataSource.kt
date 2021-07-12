package com.example.producity.models.source.remote

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.Activity

interface IActivityRemoteDataSource {
    suspend fun fetchUpcomingActivities(username: String): List<Activity>
    fun fetchPastActivity(username: String): MutableLiveData<List<Activity>>
    fun manageActivity(activity: Activity)
    fun removeParticipant(username: String, docId: String)
    fun addParticipant(username: String, docId: String)
    suspend fun getNextActivity(username: String): Activity
}