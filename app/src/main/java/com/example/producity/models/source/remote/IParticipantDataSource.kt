package com.example.producity.models.source.remote

import com.example.producity.models.Participant

interface IParticipantDataSource {
    fun addToDataBase(user: Participant, docId: String)
    fun updateList(documentId: String): List<Participant>
    fun removeFromDatabase(username: String, docId: String)
}