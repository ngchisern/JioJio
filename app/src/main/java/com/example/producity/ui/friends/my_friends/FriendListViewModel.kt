package com.example.producity.ui.friends.my_friends

import android.util.Log
import androidx.lifecycle.*
import com.example.producity.models.User
import com.example.producity.models.source.IUserRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FriendListViewModel(private val userRepository: IUserRepository) : ViewModel() {

    // stores the friend list returned by Firestore instead of calling database code again when needed
    // call getAllFriends(username) to set the friend list
    private var _friendList: MutableLiveData<List<User>> = MutableLiveData(listOf())
    val friendList: LiveData<List<User>> = _friendList

    fun getAllFriends(username: String): LiveData<List<User>> {
        val friends: MutableLiveData<List<User>> = MutableLiveData(listOf())
        viewModelScope.launch {
            friends.value = userRepository.loadFriends(username)
            _friendList.value = friends.value
        }
        return friends
    }

    suspend fun checkUserExists(username: String): Boolean {
        val deferredBoolean: Deferred<Boolean> = viewModelScope.async {
            userRepository.checkUserExists(username)
        }
        val result = deferredBoolean.await()
        Log.d("FriendListViewModel", "checkUserExists: $result")
        return result
    }

    fun sendFriendRequest(sender: User, receiverUsername: String) {
        viewModelScope.launch { userRepository.sendFriendRequest(sender, receiverUsername) }
    }

    fun addFriend(currUser: User, friend: User) {
        viewModelScope.launch { userRepository.addFriend(currUser, friend) }
    }

    fun deleteFriend(currUser: User, friend: User) {
        viewModelScope.launch { userRepository.deleteFriend(currUser, friend) }
    }

}


@Suppress("UNCHECKED_CAST")
class FriendListViewModelFactory(private val userRepository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (FriendListViewModel(userRepository) as T)
    }
}