package com.example.producity.models.source

import com.example.producity.models.Participant
import com.example.producity.models.User
import com.example.producity.models.source.remote.IParticipantDataSource

class ParticipantRepository(
   private val participantDataSource: IParticipantDataSource
): IParticipantRepository {

    override fun addToDataBase(user: User, docId: String) {
        participantDataSource.addToDataBase(user, docId)
    }

    override fun updateList(documentId: String): List<Participant> {
        return participantDataSource.updateList(documentId)
    }

    override fun removeFromDatabase(username: String, docId: String) {
        participantDataSource.removeFromDatabase(username, docId)
    }

    override fun updateRecommendation(username: String, list: List<String>) {
        participantDataSource.updateRecommendation(username, list)
    }


}