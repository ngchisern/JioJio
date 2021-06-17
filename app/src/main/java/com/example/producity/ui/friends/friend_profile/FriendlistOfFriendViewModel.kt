package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendlistOfFriendViewModel : ViewModel() {

    val currentFriend : MutableLiveData<User> = MutableLiveData(User())

    val allFriends: MutableLiveData<List<User>> = MutableLiveData(listOf())

    private fun updateUser(user: User) {
        currentFriend.value = user
    }

    private fun updateFriendList(list : List<User>) {
        allFriends.value = list
    }

    fun loadFriendsFromDB(user: User) {
        updateUser(user)
        updateFriendList(listOf())

        val db = Firebase.firestore
        val friendlist: MutableList<User> = mutableListOf()
        val username = user.username

        db.collection("users/$username/friends")
            .orderBy("username")
            .get()
            .addOnSuccessListener {
                it.forEach { doc ->
                    val friend = doc.toObject(User::class.java)
                    friendlist.add(friend)
                }
                updateFriendList(friendlist)
            }
    }
}