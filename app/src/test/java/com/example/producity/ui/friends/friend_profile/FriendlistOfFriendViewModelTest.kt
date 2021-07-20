package com.example.producity.ui.friends.friend_profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.MainCoroutineRule
import com.example.producity.getOrAwaitValue
import com.example.producity.models.User
import com.example.producity.models.source.IUserRepository
import com.example.producity.source.FakeUserRemoteDataSource
import com.example.producity.source.FakeUserRepository
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule

class FriendlistOfFriendViewModelTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var fViewModel: FriendlistOfFriendViewModel

    // Use a fake repository to be injected into the ViewModel
    private lateinit var fakeRepo: FakeUserRepository

    @Before
    fun setupViewModel() {
        fakeRepo = FakeUserRepository(FakeUserRemoteDataSource())
        fViewModel = FriendlistOfFriendViewModel(fakeRepo)
    }

    @Test
    fun loadAndGetAllFriends_testData() {
        // When - Load some sample friends from the repository
        val friends = fViewModel.loadFriendsFromDB("testUsername").getOrAwaitValue()

        // Then - Verify that the sample friends are returned
        //val friends = fViewModel.getAllFriends().getOrAwaitValue()
        assertThat(friends[0].uid, `is`("uid0"))
        assertThat(friends[1].uid, `is`("uid1"))
    }
}