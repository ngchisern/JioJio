package com.example.producity.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.models.User
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.Before
import org.junit.Rule

class ProfileRepositoryTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule() // Use this when testing LiveData

    // Subject under test
    private lateinit var profileRepository: ProfileRepository

    @Before
    fun setupProfileRepository() {
        profileRepository = ProfileRepository()
    }

    @Test
    fun updateAndGetUserProfile_testUser() {
        // Given - A sample user
        val testUser = User("testUsername", "testId", "",
            "", "", "", "", "")

        // When - Update the user in repository
        profileRepository.updateUserProfile(testUser)

        // Then - Verify that the current user profile is updated
        val value = profileRepository.getUserProfile()
        assertThat(value, `is`(testUser))
    }
}