package com.example.producity.ui.myactivity.myactivitydetail.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.models.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val context: Fragment,
    private val username: String,
    private val chatViewmodel: ChatViewModel
) : androidx.recyclerview.widget.ListAdapter<Message, ChatAdapter.ChatViewHolder>(LOG_COMPARATOR) {


    class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.chat_message)

        //val time : TextView = view.findViewById(R.id.chat_time)
        val date: TextView = view.findViewById(R.id.chat_date)

        companion object {
            fun create(parent: ViewGroup, viewType: Int): ChatViewHolder {

                val itemView: View = if (viewType == 0) {  // from message
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_room_frommessage, parent, false)
                } else { // to message
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_room_tomessage, parent, false)
                }

                return ChatViewHolder(itemView)
            }
        }

    }

    companion object {
        private val LOG_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.message == newItem.message && oldItem.timestamp == newItem.timestamp
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val current = getItem(position)

        holder.message.text = current.message

        val isSameDate: Boolean

        if (position != 0 && current.timestamp / 86400000 == getItem(position - 1).timestamp / 86400000) {
            holder.date.textSize = 0F
            holder.date.setPadding(0)
            isSameDate = true
        } else {
            val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            holder.date.text = format.format(current.timestamp)
            isSameDate = false
        }

        if (holder.itemViewType == 0) {
            val image: ImageView = holder.view.findViewById(R.id.chat_image)
            val name: TextView = holder.view.findViewById(R.id.chat_user)

            if (position == 0 || (current.username != getItem(position - 1).username && isSameDate) || !isSameDate) {
                chatViewmodel.loadUserImage(current.username, image)
                chatViewmodel.loadNickname(current.username, name)
            } else {
                name.textSize = 0F
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val current = getItem(position)

        if (current.username == username) {
            return 1
        }
        return 0
    }


}