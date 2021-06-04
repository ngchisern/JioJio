package com.example.producity.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate

@Entity(tableName = "schedule_table")
data class ScheduleDetail(
    @ColumnInfo(name = "date") val date: LocalDate,
    @PrimaryKey val title: String,
    val time: String
)


class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimeStamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateToLong(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}