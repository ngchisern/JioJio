package com.example.producity.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.models.User
import com.example.producity.source.FakeUserRemoteDataSource
import com.example.producity.source.FakeUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Rule

@ExperimentalCoroutinesApi
class ProfileViewModelTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    // Subject under test
    private lateinit var profileViewModel: ProfileViewModel

    // Use a fake repository to be injected into the ViewModel
    private lateinit var fakeRepo: FakeUserRepository

    @Before
    fun setupViewModel() {
        fakeRepo = FakeUserRepository(FakeUserRemoteDataSource())
        profileViewModel = ProfileViewModel(fakeRepo)
    }

    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getUserProfile_returnTestUser() {
        // When - Get the user profile
        val user = profileViewModel.getUserProfile("testUsername")

        // Then - Verify that the fake user (created in fake data source) is returned
        assertThat(user.username, `is`("testUsername"))
    }

    @Test
    fun updateUserProfile_editDisplayName() {
        // Given - A sample user with edited display name
        val editedUser = User("testUsername", "testUid", "updatedDisplayName",
            "", "", "", "", "")

        // When - Update the user in ViewModel
        profileViewModel.updateUserProfile(editedUser)

        // Then - Verify that the current user profile is updated
        val value = profileViewModel.getUserProfile("testUsername")
        assertThat(value.username, `is`("testUsername"))
        assertThat(value.displayName, `is`("updatedDisplayName"))
    }

    @Test
    fun loadFriends_returnTestFriends() {
        // When - Get friend list (created in fake data source)
        val friends = profileViewModel.loadFriends("testUsername")

        // Then - Verify that the fake friend list containing two friends is returned
        assertThat(friends[0].username, `is`("friend0"))
        assertThat(friends[1].username, `is`("friend1"))
    }
}