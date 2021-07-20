package com.example.producity.ui.friends.friend_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.producity.models.Review
import com.example.producity.models.User
import com.example.producity.models.source.IUserRepository
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReviewUserViewModel(val userRepository: IUserRepository) : ViewModel() {

    var exist: Review? = null

    fun submitReview(review: Review, userProfile: User) {
        val rtdb = Firebase.database

        rtdb.getReference("review/${review.reviewee}/${review.reviewer}")
            .setValue(review)
            .addOnSuccessListener {

            }

        viewModelScope.launch {
            userRepository.editUserProfile(userProfile)
        }

    }

    suspend fun exist(reviewer: String, reviewee: String): Review? {
        val rtdb = Firebase.database

        exist = rtdb.getReference("review/$reviewee/$reviewer")
            .get()
            .await()
            .getValue(Review::class.java)

        return exist

    }

}

@Suppress("UNCHECKED_CAST")
class ReviewViewModelFactory(private val userRepository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (ReviewUserViewModel(userRepository) as T)
    }
}