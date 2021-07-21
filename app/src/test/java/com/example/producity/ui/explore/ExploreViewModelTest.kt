package com.example.producity.ui.explore

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

class ExploreViewModelTest {
    private lateinit var participantRepo: FakeTestParticipantRepository
    private lateinit var activityRepo: FakeTestActivityRepository
    private lateinit var exploreViewModel: ExploreViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

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
            "doc1" to Activity(
                "doc1", "Activity 1", "activity 1", "username0", 0, true, Date(),
                0.0, 0.0, 5, "no", listOf("username0"), listOf()
            ),
            "doc2" to Activity(
                "doc2", "Activity 2", "activity 2", "username1", 0, true, Date(),
                0.0, 0.0, 5, "yes", listOf("username1"), listOf()
            )
        )
        activityRepo.setUp(activities)

        exploreViewModel = ExploreViewModel(participantRepo, activityRepo)

    }

    @Test
    fun addParticipant() {
        val user1 = User("New1", "uid", "new1", "", 0, Date(0), "")
        val user2 = User("New2", "uid", "new1", "", 0, Date(0), "")

        exploreViewModel.addParticipant(user1, "doc1")
        exploreViewModel.addParticipant(user2, "doc2")

        assertThat(activityRepo.activities["doc1"]!!.participant.contains("New1"), `is`(true))
        assertThat(activityRepo.activities["doc2"]!!.participant.contains("New1"), `is`(false))

        assertThat(activityRepo.activities["doc1"]!!.participant.contains("New2"), `is`(false))
        assertThat(activityRepo.activities["doc2"]!!.participant.contains("New2"), `is`(true))

        assertThat(participantRepo.participants.contains("New1"), `is`(true))
        assertThat(participantRepo.participants.contains("New2"), `is`(true))


    }

}