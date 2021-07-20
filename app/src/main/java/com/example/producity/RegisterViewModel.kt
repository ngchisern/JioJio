package com.example.producity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.source.IAuthRepository
import com.example.producity.models.source.IUserRepository

class RegisterViewModel(
    private val authRepo: IAuthRepository,
    private val userRepo: IUserRepository
) : ViewModel() {

    fun isUsernameTaken(username: String): Boolean {
        return userRepo.isUsernameTaken(username)
    }

    fun createUserInFirestore(username: String, uid: String) {
        userRepo.createUser(username, uid)
    }

}

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory(
    private val authRepo: IAuthRepository,
    private val userRepo: IUserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (RegisterViewModel(authRepo, userRepo) as T)
}