package com.example.producity.models.source.remote

import android.util.Log
import com.example.producity.models.Participant
import com.example.producity.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ParticipantRemoteDataSource: IParticipantDataSource {

    override fun addToDataBase(user: User, docId: String) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/${user.username}")
            .get()
            .addOnSuccessListener {
                if(!it.exists()) {
                    val participant = Participant(user.nickname, user.username, mapOf(), listOf())
                    rtdb.getReference("participant/${user.username}")
                        .setValue(participant)
                }
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }

        rtdb.reference.child("activity/$docId/participant").push()
            .setValue(user.username)
            .addOnSuccessListener {
                Log.d("Main", "added participant")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }
    }

    override fun updateList(documentId: String): List<Participant> {
        val list = mutableListOf<Participant>()
        val rtdb = Firebase.database

        rtdb.getReference().child("activity/$documentId/participant")
            .limitToFirst(6)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        rtdb.getReference("participant/${it.value.toString()}")
                            .get()
                            .addOnSuccessListener {
                                val temp = it.getValue(Participant::class.java)  ?: return@addOnSuccessListener
                                list.add(temp)
                            }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })

        return list
    }

    override fun removeFromDatabase(username: String, docId: String) {
        val rtdb = Firebase.database

        rtdb.getReference("activity/$docId/participant")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(doc in snapshot.children) {
                        if(doc.getValue().toString().equals(username)) {
                            doc.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to read value")
                }
            })


    }

    override fun updateRecommendation(username: String, list: List<String>) {
        val rtdb = Firebase.database

        for(item in list) {
            rtdb.reference.child("participant/$username/recommendation/$item")
                .setValue(ServerValue.increment(1))

        }

    }

}