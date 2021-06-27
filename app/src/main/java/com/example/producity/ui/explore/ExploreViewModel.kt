package com.example.producity.ui.explore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.producity.models.Activity
import java.time.LocalDate

class ExploreViewModel() : ViewModel() {

    val friendActivities: MutableLiveData<List<Activity>> = MutableLiveData(listOf())

    fun getList(): List<Activity> {
        return friendActivities.value!!
    }

    fun updateList(newList: MutableList<Activity>) {
        friendActivities.value = newList
    }

}

