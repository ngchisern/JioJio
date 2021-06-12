package com.example.producity.ui.myactivity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import java.time.LocalDate


class MyActivityViewModel() : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _targetDate = MutableLiveData(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val targetDate: LiveData<LocalDate> = _targetDate

    val myActivityList: MutableLiveData<List<MyActivityListItem>> = MutableLiveData(listOf())


    fun updateList(newList: List<MyActivityListItem>) {
        myActivityList.value = newList
    }

    fun addActivity(newActivity: MyActivityListItem) {
        val temp: MutableList<MyActivityListItem> = mutableListOf()
        myActivityList.value?.let { temp.addAll(it) }
        temp.add(newActivity)
        myActivityList.value = temp

    }



}

