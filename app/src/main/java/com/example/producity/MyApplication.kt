package com.example.producity

import android.app.Application
import com.example.producity.ui.dashboard.TaskRepository
import com.example.producity.ui.home.MyRoomDatabase
import com.example.producity.ui.home.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { MyRoomDatabase.getDatabase(this, applicationScope) }
    val scheduleRepository by lazy { ScheduleRepository(database.scheduleDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }

}