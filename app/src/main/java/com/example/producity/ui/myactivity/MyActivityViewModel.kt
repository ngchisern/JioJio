package com.example.producity.ui.myactivity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import java.time.LocalDate


class MyActivityViewModel() : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _targetDate = MutableLiveData(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val targetDate: LiveData<LocalDate> = _targetDate

    @RequiresApi(Build.VERSION_CODES.O)
    val allScheduleDetail: LiveData<List<ScheduleDetail>> = MutableLiveData(listOf())

    //remove
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(year: Int, month: Int, day: Int) {
        val temp = LocalDate.of(year, month, day)
        _targetDate.value = temp
    }

    fun updateList(): List<ScheduleDetail> {
        val list = allScheduleDetail.value ?: return listOf()

        return list.filter { elem ->
            elem.date.equals(targetDate.value)
        }
    }



}

