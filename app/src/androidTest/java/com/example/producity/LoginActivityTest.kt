package com.example.producity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.producity.models.source.FakeAndroidTestAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class LoginActivityTest {
    private lateinit var repository: FakeAndroidTestAuthRepository

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUpRepository() {
        repository = FakeAndroidTestAuthRepository()
        repository.addAccount("apple@gmail.com", "applepie")
        repository.addAccount("benlee@hotmail.com", "benlee123")
        repository.addAccount("alextan@outlook.com", "ALEX123")

        ServiceLocator.authRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetAllRepositories()
    }

    @Test
    fun clickLogIn_correctInput() {
        Intents.init()

        onView(withId(R.id.login_email)).perform(typeText("apple@gmail.com"))
        onView(withId(R.id.login_password)).perform(typeText("applepie"))
        onView(withId(R.id.login_button)).perform(click())

        Intents.intended(hasComponent(MainActivity::class.java.name))

        Intents.release()

        Thread.sleep(2000)
    }

    @Test
    fun clickLogIn_wrongInput() {

        onView(withId(R.id.login_email)).perform(typeText("bee@gmail.com"))
        onView(withId(R.id.login_password)).perform(typeText("applepie"))
        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.login_error_message)).check(matches(isDisplayed()))
        onView(withId(R.id.login_error_message)).check(matches(withText("Wrong email or password")))

        Thread.sleep(2000)
    }



    @Test
    fun clickSignUp() {
        Intents.init()

        onView(withId(R.id.sign_up_text)).perform(click())

        Intents.intended(hasComponent(RegisterActivity::class.java.name))

        Intents.release()

        Thread.sleep(2000)
    }



}