package com.example.producity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.models.source.FakeTestAuthRepository
import com.example.producity.models.source.FakeTestUserRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.Before
import org.junit.Rule

class RegisterViewModelTest {
    private lateinit var authRepo: FakeTestAuthRepository
    private lateinit var userRepo: FakeTestUserRepository
    private lateinit var registerViewModel: RegisterViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUpViewModel() {
        authRepo = FakeTestAuthRepository()
        authRepo.addAccount("apple@gmail.com", "applepie")
        authRepo.addAccount("benlee@hotmail.com", "benlee123")
        authRepo.addAccount("alextan@outlook.com", "ALEX123")

        userRepo = FakeTestUserRepository()
        userRepo.buildUserData(linkedMapOf("dummy1" to "123", "ben10" to "forever 10", "Rachel Tan" to "pokemon123"))

        registerViewModel = RegisterViewModel(authRepo, userRepo)
    }

    @Test
    fun isUsernameTaken_takenUsernames() {
        assertThat(registerViewModel.isUsernameTaken("dummy1"), `is`(true))
        assertThat(registerViewModel.isUsernameTaken("ben10"), `is`(true))
        assertThat(registerViewModel.isUsernameTaken("Rachel Tan"), `is`(true))
    }

    @Test
    fun isUsernameTaken_noTakenUsernames() {
        assertThat(registerViewModel.isUsernameTaken("ahbeng"), `is`(false))
        assertThat(registerViewModel.isUsernameTaken("hello"), `is`(false))
        assertThat(registerViewModel.isUsernameTaken("ali muthu"), `is`(false))
    }
}