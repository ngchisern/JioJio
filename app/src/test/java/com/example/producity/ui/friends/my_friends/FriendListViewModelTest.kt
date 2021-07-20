package com.example.producity.ui.friends.my_friends

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.MainCoroutineRule
import com.example.producity.getOrAwaitValue
import com.example.producity.models.User
import com.example.producity.models.source.FakeTestActivityRepository
import com.example.producity.models.source.FakeTestUserRepository
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class FriendListViewModelTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var friendListViewModel: FriendListViewModel

    // Use a fake repository to be injected into the ViewModel
    private lateinit var fakeUserRepo: FakeTestUserRepository
    private lateinit var fakeActivityRepo: FakeTestActivityRepository

    @Before
    fun setupViewModel() {
        fakeUserRepo = FakeTestUserRepository()
        fakeUserRepo.buildUserData(linkedMapOf("username0" to "uid0", "username1" to "uid1"))
        fakeActivityRepo = FakeTestActivityRepository()
        friendListViewModel = FriendListViewModel(fakeUserRepo, fakeActivityRepo)
    }

    @Test
    fun getAllFriends_returnSampleFriends() {
        // When - get list of friends
        val friends = friendListViewModel.getAllFriends("username0").getOrAwaitValue()

        // Then - verify that the sample friends are returned
        assertThat(friends[0].username, `is`("friend0"))
        assertThat(friends[1].username, `is`("friend1"))
    }

    @Test
    fun checkUserExists_existingUser() = runBlockingTest {
        val user = friendListViewModel.checkUserExists("username0")

        assertThat(user?.username, `is`("username0"))
    }

    @Test
    fun checkUserExists_nonExistingUser() = runBlockingTest {
        val user = friendListViewModel.checkUserExists("nonExistingUsername")

        assertThat(user, `is`(nullValue()))
    }

    @Test
    fun addFriend_testFriend() {
        // When - add a new friend to the list
        val newFriend = User("newFriend0", "newFriendUid0")
        friendListViewModel.addFriend(User("username0"), newFriend)

        // Then - verify that the new friend is added to the list
        val friends = friendListViewModel.getAllFriends("username0").getOrAwaitValue()
        assertThat(friends.last().username, `is`("newFriend0"))
    }

    @Test
    fun deleteFriend_testFriend() {
        val currUser = User("username0")
        val newFriend = User("toBeDeleted")
        friendListViewModel.addFriend(currUser, newFriend)

        friendListViewModel.deleteFriend(currUser, newFriend)

        val friends = friendListViewModel.getAllFriends("username0").getOrAwaitValue()
        assertThat(friends.contains(newFriend), `is`(false))
    }

    @Test
    fun getNextEvent_testActivity() {
        val next = friendListViewModel.getNextEvent("ben").getOrAwaitValue()
        // based on sample data initialised in FakeTestActivityRepository

        assertThat(next.docId, `is`("doc1"))
        assertThat(next.participant.contains("ben"), `is`(true))
        assertThat(next.participant.contains("nonExisting"), `is`(false))
    }

    @Test
    fun getNextEvent_invalidUsername() {
        val next = friendListViewModel.getNextEvent("nonExisting").getOrAwaitValue()

        assertThat(next, `is`(nullValue()))
    }
}