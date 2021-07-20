package com.example.producity.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.MainCoroutineRule
import com.example.producity.models.User
import com.example.producity.models.source.FakeTestAuthRepository
import com.example.producity.models.source.FakeTestUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.Rule

@ExperimentalCoroutinesApi
class ProfileViewModelTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var profileViewModel: ProfileViewModel

    // Use a fake repository to be injected into the ViewModel
    private lateinit var fakeUserRepo: FakeTestUserRepository
    private lateinit var fakeAuthRepo: FakeTestAuthRepository

    @Before
    fun setupViewModel() {
        fakeUserRepo = FakeTestUserRepository()
        fakeUserRepo.buildUserData(linkedMapOf("username0" to "uid0", "username1" to "uid1"))
        fakeAuthRepo = FakeTestAuthRepository()
        profileViewModel = ProfileViewModel(fakeUserRepo, fakeAuthRepo)
    }

    @Test
    fun getUserProfile_returnTestUser() {
        // When - Get the user profile
        val user = profileViewModel.getUserProfile("username0")

        // Then - Verify that the fake user (created in fake data source) is returned
        assertThat(user.username, `is`("username0"))
    }

    @Test
    fun loadFriends_returnTestFriends() {
        // When - Get friend list (created in fake data source)
        val friends = profileViewModel.loadFriends("username0")

        // Then - Verify that the fake friend list containing two friends is returned
        assertThat(friends[0].username, `is`("friend0"))
        assertThat(friends[1].username, `is`("friend1"))
    }
}