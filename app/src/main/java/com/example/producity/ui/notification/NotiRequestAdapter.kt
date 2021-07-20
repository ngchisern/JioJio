package com.example.producity.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.models.*
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.firebase.Timestamp
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotiRequestAdapter(
    val context: Fragment, val myActivityViewModel: MyActivityViewModel,
    val sharedViewModel: SharedViewModel, val notificationViewModel: NotificationViewModel
) : ListAdapter<Request, NotiRequestAdapter.NotiRequestViewHolder>(NotiRequestComparator()) {

    class NotiRequestViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.sender_image)
        val name: TextView = view.findViewById(R.id.sender_name)
        val message: TextView = view.findViewById(R.id.noti_message)
        val date: TextView = view.findViewById(R.id.noti_date)
        val accept: Button = view.findViewById(R.id.accept_button)
        val cancel: Button = view.findViewById(R.id.cancel_button)
        val options: LinearLayout = view.findViewById(R.id.request_option)

        fun bind(current: Request) {
            val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

            //Picasso.get().load(current.imageUrl).transform(CropCircleTransformation()).into(image)
            date.text = dateFormat.format(current.timestamp)
            name.text = "Dummy value"

            if (current.code == Request.FRIENDREQUEST) {
                message.text = "Sent you a friend request."
            }

        }

        companion object {
            fun create(parent: ViewGroup): NotiRequestViewHolder {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.notification_request_item, parent, false)

                return NotiRequestViewHolder(itemView)
            }
        }
    }

    class NotiRequestComparator : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.requester == newItem.requester && oldItem.subject == newItem.subject
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiRequestViewHolder {
        return NotiRequestViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NotiRequestViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        notificationViewModel.loadImage(current.requester, holder.image)
        notificationViewModel.loadNickname(current.requester, holder.name)

        listen(holder, current)
    }


    private fun listen(holder: NotiRequestViewHolder, current: Request) {

        if (current.code == Request.FRIENDREQUEST) {
            holder.cancel.setOnClickListener {
                removeFriendRequest(current)
                holder.options.visibility = View.GONE
            }

            holder.accept.setOnClickListener {
                addFriend(current)
                removeFriendRequest(current)
                holder.options.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                navigateToUserProfile(current)
            }

        }

    }

    private fun removeFriendRequest(current: Request) {
        val rtdb = Firebase.database

        rtdb.getReference("request/${current.subject}/${current.requester}")
            .setValue(null)

    }


    private fun addFriend(current: Request) {
        val db = Firebase.firestore
        val sender = current.requester

        db.document("users/$sender")
            .get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    return@addOnSuccessListener
                }

                val friend = it.toObject(User::class.java) ?: return@addOnSuccessListener
                val currentUser = sharedViewModel.getUser()

                db.document("users/${currentUser.username}/friends/$sender")
                    .set(friend)

                db.document("users/$sender/friends/${currentUser.username}")
                    .set(currentUser)

                addToSocialUpdate(current)

            }
            .addOnFailureListener {
                Timber.d(it.toString())
            }
    }

    private fun addToSocialUpdate(current: Request) {
        val rtdb = Firebase.database

        val msg = "${sharedViewModel.getUser().nickname} accepted your friend request."
        val noti =
            Notification(current.requester, msg, Timestamp.now().toDate().time, current.subject)

        rtdb.getReference("notification/${current.requester}")
            .push()
            .setValue(noti)

    }

    private fun navigateToUserProfile(current: Request) {
        CoroutineScope(Dispatchers.Main).launch {
            val user = notificationViewModel.checkUserExists(current.requester)
            if (user != null) {
                //friendListViewModel.sendFriendRequest(sharedViewModel.currentUser.value!!, checkUsername)
                val action =
                    NotificationFragmentDirections.actionNotificationFragmentToFriendProfileFragment(
                        user
                    )
                context.findNavController().navigate(action)
            }
        }
    }


}