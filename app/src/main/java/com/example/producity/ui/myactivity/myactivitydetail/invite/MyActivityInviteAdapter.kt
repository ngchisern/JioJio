package com.example.producity.ui.myactivity.myactivitydetail.invite

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.models.Notification
import com.example.producity.models.Participant
import com.example.producity.models.User
import com.example.producity.ui.myactivity.myactivitydetail.MyActivityDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class MyActivityInviteAdapter(val context: Fragment, val currentUser: User, val activityDetailViewModel: MyActivityDetailViewModel):
    ListAdapter<User, MyActivityInviteAdapter.InviteViewHolder>(InviteComparator()) {

    class InviteViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friend_username)
        val pic: ImageView = view.findViewById(R.id.friend_image)
        val inviteButton: Button = view.findViewById(R.id.dialog_invite_button)

        fun bind(text: String?) {
            username.text = text
        }

        companion object {
            fun create(parent: ViewGroup): InviteViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_detail_invite_item, parent, false)
                return InviteViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        return InviteViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.nickname)

        activityDetailViewModel.loadUserImage(current.username, holder.pic)

        holder.inviteButton.setOnClickListener {
            val builder = context.context?.let { MaterialAlertDialogBuilder(it) } ?: return@setOnClickListener

            builder.setMessage("Invite ${current.nickname} to the activity?")
                .setPositiveButton("Confirm") { dialog, which ->
                    addToNotification(current)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }

            builder.show()
        }

    }

    class InviteComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }
    }

    private fun addToNotification(user: User) {

        val docId = activityDetailViewModel.currentActivity!!.docId

        val noti = Notification(user.username,
                                "${currentUser.nickname} invited you to join an activity.",
                                Timestamp.now().toDate().time,
                                docId)

        val rtdb = Firebase.database

        rtdb.reference.child("notification/${user.username}").push()
            .setValue(noti)
            .addOnSuccessListener {
                Log.d("Main", "added noti to database")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }


        val participant = User(user.username, nickname = user.nickname)

        activityDetailViewModel.addParticipant(participant, docId)


    }




}