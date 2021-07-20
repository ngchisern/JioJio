package com.example.producity.ui.friends.my_friends

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.getOrAwaitValue
import com.example.producity.models.User
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IUserRepository
import com.example.producity.source.FakeUserRemoteDataSource
import com.example.producity.source.FakeUserRepository
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule

class FriendListViewModelTest {
//    @get: Rule
//    var instantExecutorRule = InstantTaskExecutorRule()
//
//    // Subject under test
//    private lateinit var friendListViewModel: FriendListViewModel
//
//    // Use a fake repository to be injected into the ViewModel
//    private lateinit var fakeUserRepo: IUserRepository
//    private lateinit var fakeActivityRepo: IActivityRepository
//
//    @Before
//    fun setupViewModel() {
//        fakeUserRepo = FakeUserRepository(FakeUserRemoteDataSource())
//        fakeActivityRepo = FakeActivityRepository(FakeActivityRemoteDataSource())
//        friendListViewModel = FriendListViewModel(fakeUserRepo, fakeActivityRepo)
//    }
//
//    @Test
//    fun getAllFriends_returnSampleFriends() {
//        // When - get list of friends
//        val friends = friendListViewModel.getAllFriends().getOrAwaitValue()
//
//        // Then - verify that the sample friends are returned
//        assertThat(friends[0].uid, `is`("friend0"))
//        assertThat(friends[1].uid, `is`("friend1"))
//    }
//
//    @Test
//    fun updateFriendList_testList() {
//        // When - update with a new friend list
//        val newList = listOf(
//            User("NewFriend0", "newFriend0", "",
//            "", "", "", "", ""),
//            User("NewFriend1", "newFriend1", "",
//                "", "", "", "", "")
//        )
//        friendListViewModel.updateFriendList(newList)
//
//        // Then - verify that the friend list is updated
//        val friends = friendListViewModel.getAllFriends().getOrAwaitValue()
//        assertThat(friends[0].uid, `is`("newFriend0"))
//        assertThat(friends[1].uid, `is`("newFriend1"))
//    }
//
//    @Test
//    fun addFriend_testFriend() {
//        // When - add a new friend to the list
//        val newFriend = User("AddNewFriend", "addNewFriend", "",
//        "", "", "", "", "")
//        friendListViewModel.addFriend(newFriend)
//
//        // Then - verify that the new friend is added to the list
//        val friends = friendListViewModel.getAllFriends().getOrAwaitValue()
//        assertThat(friends.last().uid, `is`("addNewFriend"))
//    }
}