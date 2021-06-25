package com.example.producity.ui.friends.friend_profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.getOrAwaitValue
import com.example.producity.models.User
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule

class FriendlistOfFriendViewModelTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var fViewModel: FriendlistOfFriendViewModel

    // Use a fake repository to be injected into the ViewModel
    private lateinit var fakeRepo: IFriendlistOfFriendRepository

    @Before
    fun setupViewModel() {
        fakeRepo = FakeFriendlistOfFriendRepository()
        fViewModel = FriendlistOfFriendViewModel(fakeRepo)
    }

    @Test
    fun loadAndGetAllFriends_testData() {
        // When - Load some sample friends from the repository
        fViewModel.loadFriendsFromDB(User())

        // Then - Verify that the sample friends are returned
        val friends = fViewModel.getAllFriends().getOrAwaitValue()
        assertThat(friends[0].uid, `is`("friend0"))
        assertThat(friends[1].uid, `is`("friend1"))
    }
}