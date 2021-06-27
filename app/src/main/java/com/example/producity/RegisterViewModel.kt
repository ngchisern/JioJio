package com.example.producity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.source.IAuthRepository
import com.example.producity.models.source.IUserRepository

class RegisterViewModel(private val authRepo: IAuthRepository,
                        private val userRepo: IUserRepository): ViewModel() {

    fun isUsernameTaken(username: String): Boolean {
        return userRepo.isUsernameTaken(username)
    }

    fun createUser(username: String, email: String, password: String) {
        val uid = authRepo.createUserWithEmailAndPassword(email, password)

        userRepo.createUser(username, uid)
    }

}

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory(
    private val authRepo: IAuthRepository,
    private val userRepo: IUserRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (RegisterViewModel(authRepo, userRepo) as T)
}