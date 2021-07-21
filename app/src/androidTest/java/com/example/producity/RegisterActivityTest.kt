package com.example.producity

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.producity.models.source.FakeAndroidTestAuthRepository
import com.example.producity.models.source.FakeAndroidTestUserRepository
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class RegisterActivityTest {
    private lateinit var authRepo: FakeAndroidTestAuthRepository
    private lateinit var userRepo: FakeAndroidTestUserRepository

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setUpRepository() {
        authRepo = FakeAndroidTestAuthRepository()
        authRepo.addAccount("apple@gmail.com", "applepie")
        authRepo.addAccount("benlee@hotmail.com", "benlee123")
        authRepo.addAccount("alextan@outlook.com", "ALEX123")

        userRepo = FakeAndroidTestUserRepository()
        userRepo.buildUserData(
            linkedMapOf(
                "dummy1" to "123",
                "ben10" to "forever 10",
                "Rachel Tan" to "pokemon123"
            )
        )

        ServiceLocator.authRepository = authRepo
        ServiceLocator.userRepository = userRepo

    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetAllRepositories()
    }

    @Test
    fun clickRegister_correctInput() {
        Intents.init()

        onView(withId(R.id.signup_username)).perform(typeText("benlim"))
        onView(withId(R.id.sign_up_email)).perform(typeText("benlim@gmail.com"))
        onView(withId(R.id.sign_up_password)).perform(typeText("benlimben"))
        onView(withId(R.id.confirm_password)).perform(typeText("benlimben"), closeSoftKeyboard())


        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(click())

        Intents.intended(hasComponent(MainActivity::class.java.name))

        Intents.release()

        Thread.sleep(2000)
    }

    @Test
    fun clickRegister_emptyInputs() {

        onView(withId(R.id.sign_up_button)).perform(click())

        onView(withId(R.id.signup_username)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("Please enter your username.")))
        onView(withId(R.id.sign_up_password)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_password)).check(matches(hasErrorText("Password must contain at least 6 characters.")))
        onView(withId(R.id.sign_up_email)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_email)).check(matches(hasErrorText("Please enter your email.")))

        Thread.sleep(2000)

    }

    @Test
    fun clickRegister_wrongEmailFormat() {
        onView(withId(R.id.signup_username)).perform(typeText("benlim"))
        onView(withId(R.id.sign_up_email)).perform(typeText("benlim@gmail.com."))
        onView(withId(R.id.sign_up_password)).perform(typeText("benlimben"))
        onView(withId(R.id.confirm_password)).perform(typeText("benlimben"), closeSoftKeyboard())

        onView(withId(R.id.sign_up_button)).perform(click())

        onView(withId(R.id.sign_up_email)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_email)).check(matches(hasErrorText("Wrong email format.")))

        Thread.sleep(2000)

    }

    @Test
    fun clickRegister_differentPasswords() {
        onView(withId(R.id.signup_username)).perform(typeText("benlim"))
        onView(withId(R.id.sign_up_email)).perform(typeText("benlim@gmail.com"))
        onView(withId(R.id.sign_up_password)).perform(typeText("benlimben"))
        onView(withId(R.id.confirm_password)).perform(typeText("benlimbenn"), closeSoftKeyboard())

        onView(withId(R.id.sign_up_button)).perform(click())

        onView(withId(R.id.confirm_password)).check(matches(isDisplayed()))
        onView(withId(R.id.confirm_password)).check(matches(hasErrorText("Passwords do not match.")))

        Thread.sleep(2000)

    }

    @Test
    fun clickRegister_takenUsername() {
        onView(withId(R.id.signup_username)).perform(typeText("dummy1"))
        onView(withId(R.id.sign_up_email)).perform(typeText("benlim@gmail.com"))
        onView(withId(R.id.sign_up_password)).perform(typeText("benlimben"))
        onView(withId(R.id.confirm_password)).perform(typeText("benlimben"), closeSoftKeyboard())

        onView(withId(R.id.sign_up_button)).perform(click())

        onView(withId(R.id.signup_username)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("The username is already taken.")))

        Thread.sleep(2000)

    }

    @Test
    fun clickRegister_randomInput1() {
        onView(withId(R.id.signup_username)).perform(typeText("ben10"))
        onView(withId(R.id.sign_up_email)).perform(typeText("ben10@gmail.com.my"))
        onView(withId(R.id.sign_up_password)).perform(typeText("benlimben"))
        onView(withId(R.id.confirm_password)).perform(typeText("benlimbenlim"), closeSoftKeyboard())

        onView(withId(R.id.sign_up_button)).perform(click())

        onView(withId(R.id.confirm_password)).check(matches(isDisplayed()))
        onView(withId(R.id.confirm_password)).check(matches(hasErrorText("Passwords do not match.")))

        Thread.sleep(2000)

    }


    @Test
    fun clickSignIn() {
        Intents.init()

        Espresso.onView(ViewMatchers.withId(R.id.sign_in_text)).perform(click())

        assertTrue(activityRule.scenario.state.equals(Lifecycle.State.DESTROYED))

        Intents.release()

        Thread.sleep(2000)
    }

}