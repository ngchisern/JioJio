package com.example.producity.ui.friends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.FriendSnippet
import com.example.producity.models.User

class FriendListViewModel : ViewModel() {

    val currentUser : MutableLiveData<User> = MutableLiveData(User())

    val allFriends: MutableLiveData<List<FriendListItem>> = MutableLiveData(listOf())

    fun updateUser(user: User) {
        currentUser.value = user
    }

    fun updateFriendList(list : List<FriendListItem>) {
        allFriends.value = list
    }

    fun addFriend(friend: FriendListItem) {
        val newList: MutableList<FriendListItem> = mutableListOf()
        allFriends.value?.let { newList.addAll(it) }
        newList.add(friend)
        updateFriendList(newList)
    }
}