package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.*
import com.example.producity.models.User
import com.example.producity.models.source.IUserRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FriendlistOfFriendViewModel(private val userRepository: IUserRepository) :
    ViewModel() {

    // stores the friend list returned by Firestore instead of calling database code again when needed
    // call getAllFriends(username) to set the friend list
    private var _friendList: MutableLiveData<List<User>> = MutableLiveData(listOf())
    val friendList: LiveData<List<User>> = _friendList

    fun loadFriendsFromDB(username: String): LiveData<List<User>> {
        val friends: MutableLiveData<List<User>> = MutableLiveData(listOf())
        viewModelScope.launch {
            friends.value = userRepository.loadFriends(username)
            _friendList.value = friends.value
        }
        return friends
    }
}


@Suppress("UNCHECKED_CAST")
class FriendlistOfFriendViewModelFactory(private val userRepository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (FriendlistOfFriendViewModel(userRepository) as T)
    }
}