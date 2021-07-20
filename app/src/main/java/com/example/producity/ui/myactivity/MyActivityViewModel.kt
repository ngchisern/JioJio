package com.example.producity.ui.myactivity


import android.widget.ImageView
import androidx.lifecycle.*
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class MyActivityViewModel : ViewModel() {

    val myActivityList: MutableLiveData<List<Activity>> = MutableLiveData(listOf())
    val recommended: MutableLiveData<MutableList<Activity>> = MutableLiveData(mutableListOf())

    fun getActivity(position: Int): Activity {
        return myActivityList.value!![position]
    }

    fun updateList(newList: List<Activity>) {
        myActivityList.value = newList
    }

    fun getRecommendation(username: String) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/$username")
            .get()
            .addOnSuccessListener { it ->
                val participant =
                    it.getValue(Participant::class.java) ?: return@addOnSuccessListener
                val list = participant.recommendation.toList().sortedByDescending { it.second }

                if (list.isEmpty()) return@addOnSuccessListener

                selectRecommendation(list, 0, username)
            }
    }


    private fun selectRecommendation(list: List<Pair<String, Int>>, count: Int, username: String) {
        val db = Firebase.firestore
        val item = list[count].first

        db.collection("activity")
            .whereArrayContains("label", item)
            .limit(2)
            .get()
            .addOnSuccessListener {
                val temp = it.toObjects(Activity::class.java)

                for (act in temp) {
                    if (!act.participant.contains(username) && !recommended.value!!.contains(act)) {
                        recommended.value!!.add(act)
                    }
                }

                if (recommended.value!!.size > 3 || count + 1 >= list.size) {
                    recommended.value = recommended.value
                    return@addOnSuccessListener
                } else {
                    selectRecommendation(list, count + 1, username)
                }
            }

    }

    fun loadImage(docId: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("activity_images/${docId}")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(view)
            }
    }


}

