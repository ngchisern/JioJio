package com.example.producity.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.producity.R
import com.example.producity.RegisterActivity
import com.example.producity.ServiceLocator
import com.example.producity.models.User
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Rule

@MediumTest
@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeRepo: IProfileRepository

    @Before
    fun initRepository() {
        fakeRepo = FakeAndroidTestProfileRepository()
        ServiceLocator.profileRepository = fakeRepo
    }

    @After
    fun cleanupRepo() {
        ServiceLocator.resetAllRepositories()
    }

    @Test
    fun profileDetails_displayedInUI() {
        // Given - Set up a sample user
        val user = User(
            "testname", "testid", "TestName",
            "test_telegram", "Male", "2000-01-01", "testBio",
            RegisterActivity.BLANK_PROFILE_IMG_URL
        )
        fakeRepo.updateUserProfile(user)

        // When - Profile fragment launched
        launchFragmentInContainer<ProfileFragment>(null, R.style.Theme_ProduCity_NoActionBar)

        // Then - Profile details are displayed on screen,
        // verify that the details are shown and correct
        onView(withId(R.id.profile_pic)).check(matches(isDisplayed()))
        onView(withId(R.id.display_name)).check(matches(isDisplayed()))
        onView(withId(R.id.display_name)).check(matches(withText("TestName")))
        onView(withId(R.id.username)).check(matches(isDisplayed()))
        onView(withId(R.id.username)).check(matches(withText("testname")))
        onView(withId(R.id.telegram_handle)).check(matches(isDisplayed()))
        onView(withId(R.id.telegram_handle)).check(matches(withText("test_telegram")))
        onView(withId(R.id.birthday)).check(matches(isDisplayed()))
        onView(withId(R.id.birthday)).check(matches(withText("2000-01-01")))
        onView(withId(R.id.bio)).check(matches(isDisplayed()))
        onView(withId(R.id.bio)).check(matches(withText("testBio")))

        Thread.sleep(2000) // display screen for a longer time
    }

    @Test
    fun clickEditIcon_navigateToEditProfileFragment() {
        //TODO
    }

    @Test
    fun clickNotificationsIcon_navigateToNotificationFragment() {
        //TODO
    }
}