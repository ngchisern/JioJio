package com.example.producity.models.source.remote

import com.example.producity.models.Participant
import com.example.producity.models.User

interface IParticipantDataSource {
    fun addToDataBase(user: User, docId: String)
    fun updateList(documentId: String): List<Participant>
    fun removeFromDatabase(username: String, docId: String)
    fun updateRecommendation(username: String, list: List<String>)
}