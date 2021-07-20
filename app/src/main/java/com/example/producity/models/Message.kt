package com.example.producity.models

data class Message(
    val message: String = "No message",
    val username: String = "",
    val timestamp: Long = -1
)