package com.example.producity.ui.dashboard

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "task_table")
class Task(
    @PrimaryKey val task: String,
    val start_date: LocalDate,
    val deadline: LocalDate,
    val description: String
) {
}