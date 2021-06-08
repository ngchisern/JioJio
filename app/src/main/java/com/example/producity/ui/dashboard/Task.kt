package com.example.producity.ui.dashboard


import java.time.LocalDate

class Task(
    val task: String,
    val start_date: LocalDate,
    val deadline: LocalDate,
    val description: String
)