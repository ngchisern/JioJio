package com.example.producity.ui.explore

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.Activity

class ExploreRepository {
    var friendActivities: List<Activity>? = null

    fun updateList(newList: MutableList<Activity>) {
        friendActivities = newList
    }
}