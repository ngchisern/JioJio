package com.example.producity.models

import com.example.producity.RegisterActivity

data class Participant(val imageUrl: String = RegisterActivity.BLANK_PROFILE_IMG_URL,
                       val displayName: String = "Dummy",
                       val username: String = "Dummo",
                        val docId: String = "")