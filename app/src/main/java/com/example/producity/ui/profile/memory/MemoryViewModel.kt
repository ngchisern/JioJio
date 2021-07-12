package com.example.producity.ui.profile.memory

import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.source.IActivityRepository

class MemoryViewModel(val activityRepository: IActivityRepository): ViewModel() {

    fun getList(username: String): LiveData<List<Activity>> {

        return Transformations.map(activityRepository.getPastActivities(username)) { x -> x }

    }


}

@Suppress("UNCHECKED_CAST")
class MemoryViewModelFactory(private val activityRepository: IActivityRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (MemoryViewModel(activityRepository) as T)
    }
}