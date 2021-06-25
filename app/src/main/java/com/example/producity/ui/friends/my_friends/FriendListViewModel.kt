package com.example.producity.ui.friends.my_friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.User

class FriendListViewModel(private val friendListRepository: IFriendListRepository) : ViewModel() {

    fun getAllFriends(): LiveData<List<User>> {
        return friendListRepository.getAllFriends()
    }

    fun updateFriendList(list: List<User>) {
        friendListRepository.updateFriendList(list)
    }

    fun addFriend(friend: User) {
        friendListRepository.addFriend(friend)
    }
}


@Suppress("UNCHECKED_CAST")
class FriendListViewModelFactory(private val friendListRepository: IFriendListRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (FriendListViewModel(friendListRepository) as T)
    }
}