package com.example.producity.ui.friends.my_friends

import android.widget.ImageView
import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.User
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IUserRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FriendListViewModel(private val userRepository: IUserRepository,
                          private val activityRepository: IActivityRepository) : ViewModel() {

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

    suspend fun checkUserExists(username: String): User? {
        val deferredBoolean: Deferred<User?> = viewModelScope.async {
            userRepository.checkUserExists(username)
        }
        return deferredBoolean.await()
    }

    fun sendFriendRequest(sender: User, receiverUsername: String) {
        viewModelScope.launch { userRepository.sendFriendRequest(sender, receiverUsername) }
    }

    fun addFriend(currUser: User, friend: User) {
        viewModelScope.launch { userRepository.addFriend(currUser, friend) }
    }

    fun deleteFriend(currUser: User, friend: User) {
        val list: MutableList<User> = mutableListOf()
        list.addAll(_friendList.value!!)
        list.remove(friend)
        _friendList.value = list
        viewModelScope.launch { userRepository.deleteFriend(currUser, friend) }
    }

    fun loadImage(username: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("profile_pictures/${username}")
            .downloadUrl
            .addOnSuccessListener {  uri ->
                if(uri == null) return@addOnSuccessListener

                Picasso.get().load(uri).into(view)
            }
    }

    fun getNextEvent(username: String): MutableLiveData<Activity> {
        val activity: MutableLiveData<Activity> = MutableLiveData(Activity())
        viewModelScope.launch {
            activity.value = activityRepository.getNextActivity(username)
        }

        return activity

    }

    fun sendFriendRequest() {

    }


}


@Suppress("UNCHECKED_CAST")
class FriendListViewModelFactory(private val userRepository: IUserRepository, private val activityRepository: IActivityRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (FriendListViewModel(userRepository, activityRepository) as T)
    }
}