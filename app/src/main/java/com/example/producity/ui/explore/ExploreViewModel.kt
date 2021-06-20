package com.example.producity.ui.explore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.producity.models.Activity
import java.time.LocalDate

class ExploreViewModel() : ViewModel() {
    val friendActivities: MutableLiveData<List<Activity>> = MutableLiveData(listOf())
    val documentIds: MutableList<String> = mutableListOf()

    fun updateList(newList: MutableList<Activity>) {
        friendActivities.value = newList
    }

}

