package com.example.producity.models

import com.example.producity.RegisterActivity

data class Participant(val nickname: String = "Dummy",
                       val username: String = "Dummo",
                       val recommendation: Map<String, Int> = mapOf(),
                       val wishlist: List<String> = listOf() )