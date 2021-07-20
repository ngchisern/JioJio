package com.example.producity.models

data class Review(
    val reviewer: String = "",
    val reviewee: String = "",
    val rating: Float = -1.0F,
    val description: String = "",
    val timestamp: Long = -1
)