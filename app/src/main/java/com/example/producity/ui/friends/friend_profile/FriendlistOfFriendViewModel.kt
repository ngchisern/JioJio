package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.User
import com.example.producity.ui.profile.IProfileRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendlistOfFriendViewModel(private val fRepository: IFriendlistOfFriendRepository) :
    ViewModel() {

    fun loadFriendsFromDB(user: User) {
        fRepository.loadFriendsFromDB(user)
    }

    fun getAllFriends(): MutableLiveData<List<User>> {
        return fRepository.getAllFriends()
    }
}


@Suppress("UNCHECKED_CAST")
class FriendlistOfFriendViewModelFactory(private val fRepository: IFriendlistOfFriendRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (FriendlistOfFriendViewModel(fRepository) as T)
    }
}