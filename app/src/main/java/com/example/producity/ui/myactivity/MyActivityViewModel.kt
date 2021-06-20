package com.example.producity.ui.myactivity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.producity.models.Activity
import java.time.LocalDate


class MyActivityViewModel() : ViewModel() {

    val myActivityList: MutableLiveData<List<Activity>> = MutableLiveData(listOf())
    val documentIds: MutableList<String> = mutableListOf()

    val pastActivityList: MutableLiveData<List<Activity>> = MutableLiveData(listOf())
    val pastDocumentIds: MutableList<String> = mutableListOf()

    var listInCharge: List<Activity> = listOf()
    var documentIdInCharge: List<String> = listOf()

    fun updateList(newList: List<Activity>) {
        myActivityList.value = newList
    }

    fun addActivity(newActivity: Activity) {
        val temp: MutableList<Activity> = mutableListOf()
        myActivityList.value?.let { temp.addAll(it) }
        temp.add(newActivity)
        myActivityList.value = temp
    }

    fun updatePastList(newList: List<Activity>) {
        pastActivityList.value = newList
    }

    fun addPastActivity(newActivity: Activity) {
        val temp: MutableList<Activity> = mutableListOf()
        pastActivityList.value?.let { temp.addAll(it) }
        temp.add(newActivity)
        pastActivityList.value = temp
    }



}

