package com.example.producity.ui.myactivity.myactivitydetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.example.producity.models.User
import com.example.producity.models.source.FakeTestActivityRepository
import com.example.producity.models.source.FakeTestParticipantRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class MyActivityDetailViewModelTest {
    private lateinit var participantRepo: FakeTestParticipantRepository
    private lateinit var activityRepo: FakeTestActivityRepository
    private lateinit var myActivityDetailViewModel: MyActivityDetailViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val act1 = Activity(
        "doc1", "Activity 1", "activity 1", "username0", 0, true, Date(),
        0.0, 0.0, 5, "no", listOf("username0", "other0", "other1"), listOf()
    )

    val act2 = Activity(
        "doc2", "Activity 2", "activity 2", "username1", 0, true, Date(),
        0.0, 0.0, 5, "yes", listOf("username1", "other2", "other3"), listOf()
    )

    @Before
    fun setUpViewModel() {
        participantRepo = FakeTestParticipantRepository()
        val participants = mutableMapOf(
            "username0" to Participant(
                username = "username0",
                recommendation = mutableMapOf("rec0" to 0)
            ),
            "username1" to Participant(
                username = "username1",
                recommendation = mutableMapOf("rec1" to 1)
            )
        )

        participantRepo.setUp(participants)

        activityRepo = FakeTestActivityRepository()

        val activities = linkedMapOf(
            "doc1" to act1,
            "doc2" to act2
        )
        activityRepo.setUp(activities)

        myActivityDetailViewModel = MyActivityDetailViewModel(participantRepo, activityRepo)

    }

    @Test
    fun removeParticipant() {
        myActivityDetailViewModel.setActivity(act1)
        myActivityDetailViewModel.removeParticipant("other0")

        assertThat(activityRepo.activities[act1.docId]!!.participant.size, `is`(2))
        assertThat(activityRepo.activities[act1.docId]!!.participant.contains("other0"), `is`(false))
        assertThat(activityRepo.activities[act1.docId]!!.participant.contains("other1"), `is`(true))
        assertThat(activityRepo.activities[act1.docId]!!.participant.contains("username0"), `is`(true))

    }

    @Test
    fun updateActivity() {
        val activity = Activity(
            "doc1", "New Title", "new title", "username0", 1, true, Date(),
            0.0, 0.0, 10, "no", listOf("username0", "other0", "other1"), listOf()
        )

        myActivityDetailViewModel.updateActivity(activity)

        assertThat(activityRepo.activities["doc1"]!!, `is`(activity))
        assertThat(activityRepo.activities["doc1"]!! == act1, `is`(false))

        val activity2 = Activity(
            "doc1", "New Title1", "new title1", "username0", 2, false, Date(),
            0.0, 0.0, 10, "new activity", listOf("username0", "other0"), listOf()
        )

        myActivityDetailViewModel.updateActivity(activity2)
        assertThat(activityRepo.activities["doc1"]!! == activity, `is`(false))
        assertThat(activityRepo.activities["doc1"]!! == activity2, `is`(true))
    }

    @Test
    fun addParticipant() {
        val user1 = User("New1", "uid", "new1", "", 0, Date(0), "")
        val user2 = User("New2", "uid", "new1", "", 0, Date(0), "")

        myActivityDetailViewModel.addParticipant(user1, "doc1")
        myActivityDetailViewModel.addParticipant(user2, "doc2")

        assertThat(activityRepo.activities["doc1"]!!.participant.contains("New1"), `is`(true))
        assertThat(activityRepo.activities["doc2"]!!.participant.contains("New1"), `is`(false))

        assertThat(activityRepo.activities["doc1"]!!.participant.contains("New2"), `is`(false))
        assertThat(activityRepo.activities["doc2"]!!.participant.contains("New2"), `is`(true))

        assertThat(participantRepo.participants.contains("New1"), `is`(true))
        assertThat(participantRepo.participants.contains("New2"), `is`(true))


    }


}