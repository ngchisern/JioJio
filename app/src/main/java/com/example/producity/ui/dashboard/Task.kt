package com.example.producity.ui.dashboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task(@PrimaryKey val task: String, val deadline: String) {
}