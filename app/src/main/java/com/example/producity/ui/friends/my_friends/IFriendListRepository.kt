package com.example.producity.ui.friends.my_friends

import androidx.lifecycle.LiveData
import com.example.producity.models.User

interface IFriendListRepository {
    fun updateFriendList(list: List<User>)
    fun addFriend(friend: User)
    fun getAllFriends(): LiveData<List<User>>
}