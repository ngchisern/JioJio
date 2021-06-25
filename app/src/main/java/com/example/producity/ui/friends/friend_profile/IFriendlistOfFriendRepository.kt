package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.User

interface IFriendlistOfFriendRepository {
    fun loadFriendsFromDB(user: User)
    fun getAllFriends(): MutableLiveData<List<User>>
}