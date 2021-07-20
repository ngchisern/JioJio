package com.example.producity.models

data class Participant(
    val nickname: String = "Dummy",
    val username: String = "Dummo",
    val recommendation: Map<String, Int> = mapOf(),
    val wishlist: List<String> = listOf()
)