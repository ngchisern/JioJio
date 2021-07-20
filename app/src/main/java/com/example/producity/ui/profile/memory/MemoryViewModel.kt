package com.example.producity.ui.profile.memory

import android.widget.ImageView
import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.source.IActivityRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class MemoryViewModel(val activityRepository: IActivityRepository): ViewModel() {

    fun getList(username: String): LiveData<List<Activity>> {
        return Transformations.map(activityRepository.getPastActivities(username)) { x -> x }
    }

    fun loadActivityImage(docId: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("activity_images/${docId}")
            .downloadUrl
            .addOnSuccessListener {  uri ->
                if(uri == null) return@addOnSuccessListener

                Picasso.get().load(uri).into(view)
            }
    }


}

@Suppress("UNCHECKED_CAST")
class MemoryViewModelFactory(private val activityRepository: IActivityRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (MemoryViewModel(activityRepository) as T)
    }
}