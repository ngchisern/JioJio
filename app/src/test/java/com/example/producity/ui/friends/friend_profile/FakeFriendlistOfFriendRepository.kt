package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.User

class FakeFriendlistOfFriendRepository : IFriendlistOfFriendRepository {

    private val allFriends: MutableLiveData<List<User>> = MutableLiveData(listOf())

    override fun loadFriendsFromDB(user: User) {
        val list = listOf<User>(
            User(
                "Friend0", "friend0", "",
                "", "", "", "", ""
            ),
            User(
                "Friend1", "friend1", "",
                "", "", "", "", ""
            )
        )
        allFriends.value = list
    }

    override fun getAllFriends(): MutableLiveData<List<User>> {
        return allFriends
    }
}