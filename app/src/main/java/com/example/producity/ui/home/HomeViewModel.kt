package com.example.producity.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate


class HomeViewModel(private val repository: ScheduleRepository) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _header = MutableLiveData<String>().apply {
        value = LocalDate.now().toString()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    val header: LiveData<String> = _header

    @RequiresApi(Build.VERSION_CODES.O)
    val targetDate = MutableLiveData(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val allScheduleDetail : LiveData<List<ScheduleDetail>> = repository.allSchedule.asLiveData()

    //remove
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(year: Int, month: Int, day:Int) {
        val temp = LocalDate.of(year,month,day)
        Log.d("jayson", "${temp == LocalDate.now()}")
        _header.value = temp.toString()
        targetDate.value = temp
    }

    fun itemlist() : List<ScheduleDetail> {
        val list = allScheduleDetail.value

        if(list == null) {
            return listOf()
        }

        return list.filter { elem ->
            elem.date.equals(targetDate.value)
        }
    }

    fun insert(scheduleDetail: ScheduleDetail) = viewModelScope.launch {
        repository.insert(scheduleDetail)
    }

    fun delete(scheduleDetail: ScheduleDetail) = viewModelScope.launch {
        repository.delete(scheduleDetail)
    }

}

class HomeViewModelFactory(private val repository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}