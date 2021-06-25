package com.example.producity.ui.friends.my_friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.producity.models.User

class FakeFriendListRepository : IFriendListRepository {

    // Sample data
    private val sampleFriends = listOf(
        User(
            "Friend0", "friend0", "",
            "", "", "", "", ""
        ),
        User(
            "Friend1", "friend1", "",
            "", "", "", "", ""
        )
    )
    private val allFriends: MutableLiveData<List<User>> = MutableLiveData(sampleFriends)

    override fun updateFriendList(list: List<User>) {
        allFriends.value = list
    }

    override fun addFriend(friend: User) {
        val newList: MutableList<User> = mutableListOf()
        allFriends.value?.let { newList.addAll(it) }
        newList.add(friend)
        updateFriendList(newList)
    }

    override fun getAllFriends(): LiveData<List<User>> {
        return allFriends
    }
}