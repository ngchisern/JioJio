package com.example.producity.ui.chatlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.models.Activity
import com.example.producity.models.ChatRoom
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(private val context: Fragment, val username: String):
    ListAdapter<ChatRoom, ChatListAdapter.ChatListViewHolder>(ChatListComparator()) {

    class ChatListViewHolder(val view: View, val username: String): RecyclerView.ViewHolder(view) {
        val image: CircleImageView = view.findViewById(R.id.group_icon)
        val groupname: TextView = view.findViewById(R.id.group_name)
        val message: TextView = view.findViewById(R.id.group_lastmessage)
        val time: TextView = view.findViewById(R.id.group_time)
        val unread: CardView = view.findViewById(R.id.group_unread)

        fun bind(current: ChatRoom) {
            groupname.text = current.group
            val text = "${current.sender}: ${current.message}"
            message.text = text
            val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            time.text = format.format(current.timestamp)

            if(current.unread[username]!! <= 0) {
                unread.isVisible = false
            }

            val storage = Firebase.storage

            storage.getReference("activity_images/${current.docId}")
                .downloadUrl
                .addOnSuccessListener {  uri ->
                    Picasso.get().load(uri).into(image)
                }

        }

        companion object {
            fun create(parent: ViewGroup, username: String): ChatListViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_list_itemview, parent, false)
                return ChatListViewHolder(itemView, username)
            }
        }
    }


    class ChatListComparator: DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.message == oldItem.message
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder.create(parent, username)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener {

            Firebase.firestore.document("activity/${current.docId}")
                .get()
                .addOnSuccessListener {
                    if(!it.exists()) return@addOnSuccessListener

                    val event = it.toObject(Activity::class.java)!!
                    val action = ChatListFragmentDirections.actionNavigationChatToMyActivityLogFragment(event)
                    context.findNavController().navigate(action)
                }

        }
    }
}