package com.example.producity.models.source

import com.example.producity.models.Participant
import com.example.producity.models.User

class FakeTestParticipantRepository : IParticipantRepository {

    var participants = mutableMapOf(
        "username0" to Participant(username = "username0", recommendation = mutableMapOf("rec0" to 0)),
        "username1" to Participant(username = "username1", recommendation = mutableMapOf("rec1" to 1))
    )

    fun setUp(map: MutableMap<String, Participant>) {
        participants = map
    }

    override fun addToDataBase(user: User, docId: String) {
        val participant = Participant(username = user.username)
        participants.put(user.username, participant)
    }

    override fun updateList(documentId: String): List<Participant> {
        return participants.map { it.value }
    }

    override fun removeFromDatabase(username: String, docId: String) {
        participants.remove(username)
    }

    override fun updateRecommendation(username: String, list: List<String>) {
        val newMap = participants[username]?.recommendation?.toMutableMap()?.mapValues { x ->
            if (list.contains(x.key)) {
                x.value + 1
            } else {
                1
            }
        }

        participants[username] = Participant(username = username, recommendation = newMap!!)
    }
}