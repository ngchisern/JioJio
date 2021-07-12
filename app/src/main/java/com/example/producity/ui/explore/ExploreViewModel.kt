package com.example.producity.ui.explore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IParticipantRepository
import com.example.producity.models.source.ParticipantRepository
import com.example.producity.ui.myactivity.myactivitydetail.MyActivityDetailViewModel
import java.time.LocalDate

class ExploreViewModel(private val participantRepository: IParticipantRepository,
                       private val activityRepository: IActivityRepository) : ViewModel() {

    val friendActivities: MutableLiveData<List<Activity>> = MutableLiveData(listOf())

    fun getList(): List<Activity> {
        return friendActivities.value!!
    }

    fun updateList(newList: MutableList<Activity>) {
        friendActivities.value = newList
    }

    fun addParticipant(user: Participant, docId: String) {
        activityRepository.addParticipant(user.username, docId)
        participantRepository.addToDataBase(user, docId)
    }

}

@Suppress("UNCHECKED_CAST")
class ExploreViewModelFactory(
    private val participantRepo: IParticipantRepository,
    private val activityRepo: IActivityRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ExploreViewModel(participantRepo, activityRepo) as T)
}

