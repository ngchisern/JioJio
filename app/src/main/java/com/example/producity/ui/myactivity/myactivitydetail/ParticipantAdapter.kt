package com.example.producity.ui.myactivity.myactivitydetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.models.Participant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class ParticipantlAdapter(val context: Fragment, val showName: Boolean, val manage: Boolean)
    : ListAdapter<Participant, ParticipantlAdapter.ParticipantViewHolder>(ParticipantComparator()){


    class ParticipantViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val image : ImageView = view.findViewById(R.id.participant_image)
        val name: TextView = view.findViewById(R.id.participant_name)
        val remove: ImageView = view.findViewById(R.id.remove_icon)

        fun bind(current: Participant, showName: Boolean, manage: Boolean) {
            Picasso.get().load(current.imageUrl).transform(CropCircleTransformation()).into(image)

            if(showName) {
                name.setText(current.displayName)
            } else {
                name.setText("")
            }

            if(manage) {
                remove.setBackgroundResource(R.drawable.ic_cancel_24)
                remove.setOnClickListener {
                    removeFromFirestore(current)
                }
            }

        }

        private fun removeFromFirestore(user: Participant) {
            val db = Firebase.firestore

            val delete = hashMapOf<String, Any>(
                "participant" to FieldValue.arrayRemove(user.username)
            )

            db.document("activity/${user.docId}")
                .update(delete)
                .addOnSuccessListener {
                    Log.d("Main", "Removed from firestore")
                    removeFromDatabase(user)
                }
                .addOnFailureListener {
                    Log.d("Main", it.message.toString())
                }

        }


        private fun removeFromDatabase(user: Participant) {
            val rtdb = Firebase.database

            rtdb.getReference("participant/${user.docId}")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(doc in snapshot.children) {
                            if(doc.getValue(Participant::class.java)?.username.equals(user.username)) {
                                doc.ref.removeValue()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Main", "failed to read value")
                    }

                })

        }

        companion object {
            fun create(parent: ViewGroup): ParticipantViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_participant_item_view, parent, false)
                return ParticipantViewHolder(itemView)
            }
        }
    }

    class ParticipantComparator : DiffUtil.ItemCallback<Participant>() {
        override fun areItemsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem.username == newItem.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current, showName, manage)
    }


}