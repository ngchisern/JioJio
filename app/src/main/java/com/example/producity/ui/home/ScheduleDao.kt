package com.example.producity.ui.home

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_table ORDER BY time ASC")
    fun getSchedules(): Flow<List<ScheduleDetail>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(schedule: ScheduleDetail)

    @Query("Delete FROM schedule_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(scheduleDetail: ScheduleDetail)
}