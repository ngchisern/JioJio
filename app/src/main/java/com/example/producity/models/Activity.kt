package com.example.producity.models

import java.util.*

data class Activity(val docId: String = "No doc Id",
                    val imageUrl: String = "https://i.pinimg.com/originals/b0/77/b0/b077b011f95966067bc525d3b4fa5e8e.jpg",
                    val title: String = "No Title",
                    val owner: String = "No Owner",
                    val ownerImageUrl: String = "https://i.pinimg.com/originals/b0/77/b0/b077b011f95966067bc525d3b4fa5e8e.jpg",
                    @field:JvmField
                    val isPublic: Boolean = false,
                    @field:JvmField
                    val isVirtual: Boolean = false,
                    val date: Date = Date(2021-1900,1-1,1,12,0),
                    val location: String = "Delhi",
                    val pax: Int = 1,
                    val description: String = "No Description",
                    val participant: List<String> = listOf(),
                    val viewers: List<String> = listOf()
                    )