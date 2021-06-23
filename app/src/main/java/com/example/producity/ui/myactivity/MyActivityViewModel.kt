package com.example.producity.ui.myactivity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.producity.models.Activity
import java.time.LocalDate


class MyActivityViewModel() : ViewModel() {

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

