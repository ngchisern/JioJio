package com.example.producity.ui.myactivity


import androidx.lifecycle.*
import com.example.producity.models.Activity

class MyActivityViewModel() : ViewModel() {

    val myActivityList: MutableLiveData<List<Activity>> = MutableLiveData(listOf())

    fun getActivity(position: Int): Activity {
        return myActivityList.value!!.get(position)
    }

    fun updateList(newList: List<Activity>) {
        myActivityList.value = newList
    }

}

