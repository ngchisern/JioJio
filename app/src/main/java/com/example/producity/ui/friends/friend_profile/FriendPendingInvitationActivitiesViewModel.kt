package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.example.producity.ui.myactivity.MyActivityListItem

class FriendPendingInvitationActivitiesViewModel : ViewModel() {

    val currentFriend: MutableLiveData<User> = MutableLiveData(User())

    //    val pendingInvitationActivitiesList: MutableLiveData<List<_root_ide_package_.com.example.producity.models.Activity>> = MutableLiveData(listOf())
    val pendingInvitationActivitiesList: MutableLiveData<List<Activity>> =
        MutableLiveData(
            listOf(
                Activity(
                    "https://firebasestorage.googleapis.com/v0/b/jiojio-6a358.appspot.com/o/profile_pictures%2Fblank-profile-picture.png?alt=media&token=7b7b2083-ee3f-493e-bcaf-bab365427694",
                    "Pending1", "tai", "pending time", 9999,
                    listOf("Jayson Lam ")
                )
            )
        ) // sample data


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