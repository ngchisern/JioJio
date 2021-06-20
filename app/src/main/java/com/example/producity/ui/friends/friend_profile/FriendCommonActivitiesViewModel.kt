package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendCommonActivitiesViewModel : ViewModel() {

    val currentFriend: MutableLiveData<User> = MutableLiveData(User())

    val commonActivitiesList: MutableLiveData<List<Activity>> = MutableLiveData(listOf())

    private fun updateCurrentFriend(friend: User) {
        currentFriend.value = friend
    }

    private fun updateList(newList: List<Activity>) {
        commonActivitiesList.value = newList
    }

    fun loadCommonActivitiesFromDB(currentUser: User, friend: User) {
        updateCurrentFriend(friend)
        updateList(listOf())

        val db = Firebase.firestore
        val commons: MutableList<Activity> = mutableListOf()
        val currentUsername = currentUser.username
        val friendUsername = friend.username

        db.collection("activity")
            .whereEqualTo("participants.$currentUsername", true)
            .whereEqualTo("participants.$friendUsername", true)
            .get()
            .addOnSuccessListener {
                it.forEach { doc ->
                    val commonActivity = doc.toObject(Activity::class.java)
                    commons.add(commonActivity)
                }
                updateList(commons)
            }
    }

}