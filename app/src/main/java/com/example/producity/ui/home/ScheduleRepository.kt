package com.example.producity.ui.home

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    val allSchedule : Flow<List<ScheduleDetail>> = scheduleDao.getSchedules(LocalDate.now())
     */

    val allSchedule : Flow<List<ScheduleDetail>> = scheduleDao.getSchedules()

    @SuppressWarnings("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(scheduleDetail: ScheduleDetail) {
        scheduleDao.insert(scheduleDetail)
    }

    @SuppressWarnings("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(scheduleDetail: ScheduleDetail) {
        scheduleDao.delete(scheduleDetail)
    }
}