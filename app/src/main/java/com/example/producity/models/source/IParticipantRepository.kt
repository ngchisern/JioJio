package com.example.producity.models.source

import com.example.producity.models.Participant

interface IParticipantRepository {
    fun addToDataBase(user: Participant, docId: String)
    fun updateList(documentId: String): List<Participant>
    fun removeFromDatabase(username: String, docId: String)
}