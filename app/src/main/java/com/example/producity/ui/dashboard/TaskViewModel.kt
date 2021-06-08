package com.example.producity.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel() : ViewModel() {
    // Using LiveData and caching what allTasks returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTasks: LiveData<List<Task>> = MutableLiveData(listOf())

    @RequiresApi(Build.VERSION_CODES.O)
    var newTaskDeadline: LocalDate = LocalDate.now()
    // used in DashboardFragment - getNewTask(), value updated in DatePickerFragment onDateSet()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */



}

