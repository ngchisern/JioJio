package com.example.producity.ui.myactivity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.models.Activity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class MyActivityViewModelTest {
    private lateinit var myActivityViewModel: MyActivityViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUpViewModel() {
        myActivityViewModel = MyActivityViewModel()
    }

    @Test
    fun getActivityAndUpdateList() {
        val act1 = Activity("doc1", "act1", "act1", "owner1",  0, false, Date(0)
            , 50.0, 50.0, 5, "des1", listOf("owner1"), listOf())

        val act2 = Activity("doc2", "act2", "act2", "owner2",  1, false, Date(100)
            , 100.0, 100.0, 10, "des2", listOf("owner2", "owner1"), listOf())

        val act3 = Activity("doc3", "act3", "act3", "owner3",  2, true, Date(200)
            , 150.0, 150.0, 15, "des3", listOf("owner3"), listOf())

        val list = mutableListOf<Activity>(act1, act2, act3)

        myActivityViewModel.updateList(list)

        assertThat(myActivityViewModel.getActivity(0), `is`(act1))
        assertThat(myActivityViewModel.getActivity(1), `is`(act2))
        assertThat(myActivityViewModel.getActivity(2), `is`(act3))
    }
}