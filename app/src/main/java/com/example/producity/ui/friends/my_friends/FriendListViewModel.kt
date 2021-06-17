package com.example.producity.ui.friends.my_friends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.User

class FriendListViewModel : ViewModel() {

    val currentUser : MutableLiveData<User> = MutableLiveData(User())

    val allFriends: MutableLiveData<List<User>> = MutableLiveData(listOf())

    fun updateUser(user: User) {
        currentUser.value = user
    }

    fun updateFriendList(list : List<User>) {
        allFriends.value = list
    }

    fun addFriend(friend: User) {
        val newList: MutableList<User> = mutableListOf()
        allFriends.value?.let { newList.addAll(it) }
        newList.add(friend)
        updateFriendList(newList)
    }
}