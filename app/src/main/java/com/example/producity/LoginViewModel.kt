package com.example.producity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.producity.models.source.IAuthRepository

class LoginViewModel(private val authRepository: IAuthRepository): ViewModel() {

    fun logIn(email: String, password: String): Boolean {
        return authRepository.signInWithEmailAndPassword(email, password)
    }

}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val repository: IAuthRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (LoginViewModel(repository) as T)
}
