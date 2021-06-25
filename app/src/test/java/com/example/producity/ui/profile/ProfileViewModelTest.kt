package com.example.producity.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.models.User
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.Rule

class ProfileViewModelTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var profileViewModel: ProfileViewModel

    // Use a fake repository to be injected into the ViewModel
    private lateinit var fakeRepo: FakeProfileRepository

    @Before
    fun setupViewModel() {
        fakeRepo = FakeProfileRepository()
        profileViewModel = ProfileViewModel(fakeRepo)
    }

    @Test
    fun updateUserProfile_testUser() {
        // Given - A sample user
        val testUser = User("testUsername", "testId", "",
            "", "", "", "", "")

        // When - Update the user in ViewModel
        profileViewModel.updateUserProfile(testUser)

        // Then - Verify that the current user profile is updated
        val value = profileViewModel.getUserProfile()
        assertThat(value, `is`(testUser))
    }
}