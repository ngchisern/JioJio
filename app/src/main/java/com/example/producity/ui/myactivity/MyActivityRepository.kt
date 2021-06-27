package com.example.producity.ui.myactivity

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.Activity

class MyActivityRepository {
    val myActivityList: MutableLiveData<List<Activity>> = MutableLiveData(listOf())

    val pastActivityList: MutableLiveData<List<Activity>> = MutableLiveData(listOf())

    var isUpcoming = true

    fun updateList(newList: List<Activity>) {
        myActivityList.value = newList
    }

    fun updatePastList(newList: List<Activity>) {
        pastActivityList.value = newList
    }
}