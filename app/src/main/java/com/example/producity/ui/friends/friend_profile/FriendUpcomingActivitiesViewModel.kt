package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IUserRepository
import kotlinx.coroutines.launch

class FriendUpcomingActivitiesViewModel(private val activityRepository: IActivityRepository) :
    ViewModel() {

    private var _upcomingActivities: MutableLiveData<List<Activity>> = MutableLiveData(listOf())
    val upcomingActivities: LiveData<List<Activity>> = _upcomingActivities

    fun getUpcomingActivities(username: String): LiveData<List<Activity>> {
        val activities: MutableLiveData<List<Activity>> = MutableLiveData(listOf())
        viewModelScope.launch {
            activities.value = activityRepository.getUpcomingActivities(username)
            _upcomingActivities.value = activities.value
        }
        return activities
    }
}


@Suppress("UNCHECKED_CAST")
class FriendUpcomingActivitiesViewModelFactory(private val activityRepository: IActivityRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (FriendUpcomingActivitiesViewModel(activityRepository) as T)
    }
}