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

    @Test
    fun createUser() {
        registerViewModel.createUser("gorgor", "gorgor@hotmail.com", "gorgorno1")
        registerViewModel.createUser("dododo", "dodo@hotmail.com", "dododo")
        registerViewModel.createUser("Alice Tan", "alice20@hotmail.com", "iambeautiful")

        assertThat(authRepo.authData["gorgor@hotmail.com"] == "gorgorno1", `is`(true))
        assertThat(userRepo.userData["gorgor"] == "random", `is`(true))
        assertThat(authRepo.authData["dodo@hotmail.com"] == "dododo", `is`(true))
        assertThat(userRepo.userData["dododo"] == "random", `is`(true))
        assertThat(authRepo.authData["alice20@hotmail.com"] == "iambeautiful", `is`(true))
        assertThat(userRepo.userData["Alice Tan"] == "random", `is`(true))

        assertThat(authRepo.authData["alice20@hotmail.com"] == "iamugly", `is`(false))
        assertThat(userRepo.userData["Alice Tan"] == "Alice Tan", `is`(false))
    }
}