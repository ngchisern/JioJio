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
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class MyActivityInviteAdapter(val context: Fragment, val sharedViewModel: SharedViewModel, val doc: String):
    ListAdapter<User, MyActivityInviteAdapter.InviteViewHolder>(InviteComparator()) {

    class InviteViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friend_username)
        val pic: ImageView = view.findViewById(R.id.friend_image)
        val inviteButton: Button = view.findViewById(R.id.dialog_invite_button)

        fun bind(text: String?, imageUrl: String) {
            username.text = text
            Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(pic)
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
        holder.bind(current.username, current.imageUrl)

        holder.inviteButton.setOnClickListener {
            val builder = context.context?.let { MaterialAlertDialogBuilder(it) } ?: return@setOnClickListener

            builder.setMessage("Invite ${current.username} to the activity?")
                .setPositiveButton("Yes") { dialog, which ->
                    addToActivity(current)
                    addToNotification(current)
                }
                .setNegativeButton("No") { dialog, which ->
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

    private fun addToActivity(user: User) {
        val db = Firebase.firestore

        val union = hashMapOf<String, Any>(
            "participant" to FieldValue.arrayUnion(user.username)
        )

        db.document("activity/$doc")
            .update(union)
            .addOnSuccessListener {
                Log.d("Main", "Invited to firestore")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }

    }

    private fun addToNotification(user: User) {
        val sender = sharedViewModel.currentUser.value ?: return

        val noti = Notification(sender.imageUrl,
                                "${sender.username} invited to join an activity",
                                false,
                                null,
                                Timestamp.now().toDate().time,
                                doc)

        val rtdb = Firebase.database

        rtdb.getReference().child("notification/${user.username}").push()
            .setValue(noti)
            .addOnSuccessListener {
                Log.d("Main", "added noti to database")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }

        val participant = Participant(user.imageUrl, user.displayName, user.username, doc)

        rtdb.getReference().child("participant/$doc").push()
            .setValue(participant)
            .addOnSuccessListener {
                Log.d("Main", "added participant to database")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }
    }


}