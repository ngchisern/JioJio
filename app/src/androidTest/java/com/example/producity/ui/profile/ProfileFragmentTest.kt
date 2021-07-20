package com.example.producity.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.producity.ServiceLocator
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import com.example.producity.AndroidTestMainCoroutineRule
import com.example.producity.models.source.FakeAndroidTestUserRepository
import com.example.producity.models.source.IUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = AndroidTestMainCoroutineRule()

    private lateinit var fakeRepo: IUserRepository

    @Before
    fun initRepository() = runBlockingTest {
        fakeRepo = FakeAndroidTestUserRepository()
        ServiceLocator.userRepository = fakeRepo // TODO use shared repo

//        // Set up a sample user profile // TODO Now not working, shared repo not done
//        val user = User(
//            "testUsername", "testUid", "testDisplayName",
//            "test_tele", "Male", "2000-01-01",
//            "testBio", RegisterActivity.BLANK_PROFILE_IMG_URL
//        )
//        fakeRepo.editUserProfile(user)
    }

    @After
    fun cleanupRepo() {
        ServiceLocator.resetAllRepositories()
    }

//    @Test
//    fun profileDetails_displayedInUI() {
//        // When - Profile fragment launched
//        launchFragmentInContainer<ProfileFragment>(null, R.style.Theme_ProduCity_NoActionBar)
//
//        // Then - Profile details are displayed on screen,
//        // verify that the details are shown and correct
//        onView(withId(R.id.profile_pic)).check(matches(isDisplayed()))
//        onView(withId(R.id.display_name)).check(matches(isDisplayed()))
////        onView(withId(R.id.display_name)).check(matches(withText("testDisplayName")))
//        onView(withId(R.id.username)).check(matches(isDisplayed()))
////        onView(withId(R.id.username)).check(matches(withText("testUsername")))
//        onView(withId(R.id.gender)).check(matches(isDisplayed()))
////        onView(withId(R.id.gender)).check(matches(withText("Male")))
//        onView(withId(R.id.telegram_handle)).check(matches(isDisplayed()))
////        onView(withId(R.id.telegram_handle)).check(matches(withText("test_tele")))
//        onView(withId(R.id.birthday)).check(matches(isDisplayed()))
////        onView(withId(R.id.birthday)).check(matches(withText("2000-01-01")))
//        onView(withId(R.id.bio)).check(matches(isDisplayed()))
////        onView(withId(R.id.bio)).check(matches(withText("testBio")))
//        // Commented out lines should pass when shared view model and repo is implemented
//
//        Thread.sleep(2000) // display screen for a longer time
//    }
//
//    @Test
//    fun clickEditIcon_navigateToEditProfileFragment() {
//        // Given - On the ProfileFragment page
//        val scenario =
//            launchFragmentInContainer<ProfileFragment>(null, R.style.Theme_ProduCity_NoActionBar)
//        val navController = mock(NavController::class.java)
//        scenario.onFragment {
//            Navigation.setViewNavController(it.view!!, navController)
//        }
//
//        // When - Click on the Edit icon
//        onView(withId(R.id.edit_profile)).perform(click())
//
//        // Then - Verify that we navigate to EditProfileFragment
//        verify(navController).navigate(
//            ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment()
//        )
//    }
//
//    @Test
//    fun clickNotificationsIcon_navigateToNotificationFragment() {
//        // Given - On the ProfileFragment page
//        val scenario =
//            launchFragmentInContainer<ProfileFragment>(null, R.style.Theme_ProduCity_NoActionBar)
//        val navController = mock(NavController::class.java)
//        scenario.onFragment {
//            Navigation.setViewNavController(it.view!!, navController)
//        }
//
//        // When - Click on the Notifications icon
//        onView(withId(R.id.notification)).perform(click())
//
//        // Then - Verify that we navigate to NotificationFragment
//        verify(navController).navigate(
//            ProfileFragmentDirections.actionNavigationProfileToNotificationFragment()
//        )
//    }
}