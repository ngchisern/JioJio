package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import com.example.producity.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendlistOfFriendRepository : IFriendlistOfFriendRepository {

    private val currentFriend: MutableLiveData<User> = MutableLiveData(User())

    private val allFriends: MutableLiveData<List<User>> = MutableLiveData(listOf())

    private fun updateUser(user: User) {
        currentFriend.value = user
    }

    private fun updateFriendList(list: List<User>) {
        allFriends.value = list
    }

    override fun loadFriendsFromDB(user: User) {
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

    override fun getAllFriends(): MutableLiveData<List<User>> {
        return allFriends
    }
}