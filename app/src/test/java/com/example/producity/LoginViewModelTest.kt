package com.example.producity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.models.source.FakeTestAuthRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    private lateinit var authRepository: FakeTestAuthRepository
    private lateinit var loginViewModel: LoginViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUpViewModel() {
        authRepository = FakeTestAuthRepository()
        authRepository.addAccount("apple@gmail.com", "applepie")
        authRepository.addAccount("benlee@hotmail.com", "benlee123")
        authRepository.addAccount("alextan@outlook.com", "ALEX123")

        loginViewModel = LoginViewModel(authRepository)
    }

    @Test
    fun signInWithEmailAndPassword_correctInput() {
        assertThat(loginViewModel.logIn("apple@gmail.com", "applepie"), `is`(true))
        assertThat(loginViewModel.logIn("benlee@hotmail.com", "benlee123"), `is`(true))
        assertThat(loginViewModel.logIn("alextan@outlook.com", "ALEX123"), `is`(true))
    }

    @Test
    fun signInWithEmailAndPassword_emptyInput() {
        assertThat(loginViewModel.logIn("apple@gmail.com", ""), `is`(false))
        assertThat(loginViewModel.logIn("", "benlee123"), `is`(false))
        assertThat(loginViewModel.logIn("", ""), `is`(false))
    }

    @Test
    fun signInWithEmailAndPassword_wrongInput() {
        assertThat(loginViewModel.logIn("apple@gmail.com", "applePie"), `is`(false))
        assertThat(loginViewModel.logIn("BENLEE@hotmail", "benlee123"), `is`(false))
        assertThat(loginViewModel.logIn("alextan@outlook.com", "alex123"), `is`(false))
    }


}