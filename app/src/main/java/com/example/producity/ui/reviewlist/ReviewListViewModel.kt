package com.example.producity.ui.reviewlist

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.producity.models.Participant
import com.example.producity.models.Review
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class ReviewListViewModel : ViewModel() {
    val reviews: MutableLiveData<List<Review>> = MutableLiveData(listOf())

    fun updateList(username: String) {
        val rtdb = Firebase.database

        rtdb.getReference("review/$username")
            .get()
            .addOnSuccessListener {
                val list = it.children.map { x ->
                    x.getValue(Review::class.java)!!
                }

                reviews.value = list
            }

    }

    fun loadImage(username: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("profile_pictures/${username}")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(view)
            }
    }

    fun loadNickname(username: String, view: TextView) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/$username")
            .get()
            .addOnSuccessListener {
                if (!it.exists()) return@addOnSuccessListener

                view.text = it.getValue(Participant::class.java)!!.nickname
            }
    }
}