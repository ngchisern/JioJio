package com.example.producity.ui.notification

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.models.Activity
import com.example.producity.models.Notification
import com.example.producity.models.User
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SocialUpdateAdapter(val context: Fragment, val myActivityViewModel: MyActivityViewModel,
                          val sharedViewModel: SharedViewModel, val notificationViewModel: NotificationViewModel)
    : ListAdapter<Notification, SocialUpdateAdapter.SocialUpdateViewHolder>(SocialUpdateComparator()){

    class SocialUpdateViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.sender_image)
        val message: TextView = view.findViewById(R.id.noti_message)
        val date: TextView = view.findViewById(R.id.noti_date)

        val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

        fun bind(current: Notification) {
            message.text = current.message
            date.text = dateFormat.format(current.timestamp)
        }

        companion object {
            fun create(parent: ViewGroup): SocialUpdateViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.notification_update_item, parent, false)

                return SocialUpdateViewHolder(itemView)
            }
        }
    }

    class SocialUpdateComparator : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return false
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialUpdateViewHolder {
        return SocialUpdateViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SocialUpdateViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        notificationViewModel.loadImage(current.docId!!, holder.image)
    }

    /*
    private fun listen(view: View, position: Int) {
        val accept: Button = view.findViewById(R.id.accept_button)
        val cancel: Button = view.findViewById(R.id.cancel_button)

        Log.d("Notification", getItem(position).docId!!)

        cancel.setOnClickListener {
            updateRtdb(false, position)
        }

        accept.setOnClickListener {
            updateRtdb(true, position)
            addFriend(getItem(position).docId!!)
        }
    }


    private fun addFriend(sender: String) {
        val db = Firebase.firestore

        db.document("users/$sender")
            .get()
            .addOnSuccessListener {
                if (it == null || !it.exists()) {
                    Log.d("Main", "No doc")
                    return@addOnSuccessListener
                }

                val friend = it.toObject(User::class.java) ?: return@addOnSuccessListener

                val currentUser = sharedViewModel.currentUser.value

                if (currentUser?.username == null) {
                    return@addOnSuccessListener
                }

                db.document("users/${currentUser.username}/friends/$sender")
                    .set(friend)

                db.document("users/$sender/friends/${currentUser.username}")
                    .set(currentUser)

            }
            .addOnFailureListener {
                Log.d("Main", it.toString())
            }
    }


    private fun updateRtdb(accept: Boolean, position: Int) {
        val user = sharedViewModel.currentUser.value ?: return
        val docId = notificationViewModel.docId[position]

        val rtdb = Firebase.database

        val hashMap = hashMapOf<String, Any>(
            "hasAccept" to accept
        )

        rtdb.getReference("notification/${user.username}/$docId")
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Log.d("Main", "responed to friend request")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }
    }

    private fun navigateToEvent(docId: String) {
        val db = Firebase.firestore

        db.document("activity/$docId")
            .get()
            .addOnSuccessListener {
                if(!it.exists()) {
                    return@addOnSuccessListener
                }
                val event = it.toObject(Activity::class.java)!!
                val action = NotificationFragmentDirections.actionNotificationFragmentToScheduleDetailFragment(event)
                context.findNavController().navigate(action)
            }
    }

     */

}