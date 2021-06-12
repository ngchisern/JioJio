package com.example.producity.ui.explore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.producity.ui.myactivity.MyActivityListItem
import java.time.LocalDate

class ExploreViewModel() : ViewModel() {
    // Using LiveData and caching what allTasks returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val friendActivities: MutableLiveData<List<ExploreListItem>> = MutableLiveData(listOf())

    @RequiresApi(Build.VERSION_CODES.O)
    var newTaskDeadline: LocalDate = LocalDate.now()

    fun updateList(newList: List<ExploreListItem>) {
        friendActivities.value = newList
    }

}

