package com.example.producity.ui.friends.my_friends

import android.widget.ImageView
import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.Request
import com.example.producity.models.Review
import com.example.producity.models.User
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IUserRepository
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FriendListViewModel(
    private val userRepository: IUserRepository,
    private val activityRepository: IActivityRepository
) : ViewModel() {

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
            .addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(view)
            }
    }

    fun loadActivityImage(docId: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("activity_images/${docId}")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(view)
            }
    }

    fun getNextEvent(username: String): MutableLiveData<Activity> {
        val activity: MutableLiveData<Activity> = MutableLiveData(Activity(lowerCaseTitle = ""))
        viewModelScope.launch {
            activity.value = activityRepository.getNextActivity(username)
        }

        return activity

    }

    fun sendFriendRequest(request: Request) {
        val rtdb = Firebase.database

        rtdb.getReference("request/${request.subject}/${request.requester}")
            .setValue(request)
            .addOnSuccessListener {

            }
    }

    suspend fun doesRequestExist(requester: String, requestee: String): Boolean {
        val rtdb = Firebase.database

        val exist = rtdb.getReference("request/$requestee/$requester")
            .get()
            .await()
            .getValue(Review::class.java)

        return exist != null

    }


}


@Suppress("UNCHECKED_CAST")
class FriendListViewModelFactory(
    private val userRepository: IUserRepository,
    private val activityRepository: IActivityRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (FriendListViewModel(userRepository, activityRepository) as T)
    }
}