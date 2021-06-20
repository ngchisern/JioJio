package com.example.producity.models

data class ActivityLog(val opUrl: String = "https://i.pinimg.com/originals/b0/77/b0/b077b011f95966067bc525d3b4fa5e8e.jpg",
                       val op: String = "Li Xue",
                       val stage: Int = 0,
                       val subject: String = "Ingredient",
                       val content: String = "Buy Best White Chicken",
                       val mention: List<String> = listOf())