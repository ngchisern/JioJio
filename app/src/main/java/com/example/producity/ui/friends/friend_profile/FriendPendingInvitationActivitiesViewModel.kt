package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.Activity
import com.example.producity.models.User

class FriendPendingInvitationActivitiesViewModel : ViewModel() {

    val currentFriend: MutableLiveData<User> = MutableLiveData(User())

    //    val pendingInvitationActivitiesList: MutableLiveData<List<_root_ide_package_.com.example.producity.models.Activity>> = MutableLiveData(listOf())
    val pendingInvitationActivitiesList: MutableLiveData<List<Activity>> =
        MutableLiveData(
            listOf()
            )



    private fun updateCurrentFriend(friend: User) {
        currentFriend.value = friend
    }

    private fun updateList(newList: List<Activity>) {
        pendingInvitationActivitiesList.value = newList
    }

    // TODO
    fun loadPendingInvitationsFromDB(currentUser: User, friend: User) {

    }
}