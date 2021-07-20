package com.example.producity.models

data class ChatRoom(
    val timestamp: Long = -1,
    val group: String = "",
    val docId: String = "",
    val sender: String = "",
    val message: String = "",
    val unread: Map<String, Int> = mapOf()
)
