package com.example.producity.ui.home

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.producity.ui.dashboard.Task
import com.example.producity.ui.dashboard.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(ScheduleDetail::class, Task::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyRoomDatabase : RoomDatabase(){

    abstract fun scheduleDao() : ScheduleDao
    abstract fun taskDao() : TaskDao

    companion object {
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context, scope : CoroutineScope) : MyRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "my_room_database"
                ).addCallback(MyRoomDatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class MyRoomDatabaseCallback (private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let{ database ->
                scope.launch {
                    populateDatabase(database.scheduleDao(), database.taskDao())
                }
            }
        }

        suspend fun populateDatabase(scheduleDao: ScheduleDao, taskDao: TaskDao) {
            scheduleDao.deleteAll()
            taskDao.deleteAll()
        }
    }
}