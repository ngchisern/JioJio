package com.example.producity.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R

class FriendListAdapter(val context: Fragment):
    ListAdapter<FriendListItem, FriendListAdapter.FriendsViewHolder>(FriendsComparator()) {

    class FriendsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friend_username)

        fun bind(text: String?) {
            username.text = text
        }

        companion object {
            fun create(parent: ViewGroup): FriendsViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.friend_list_view_item, parent, false)
                return FriendsViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListAdapter.FriendsViewHolder {
        return FriendsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FriendListAdapter.FriendsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.username)
    }



    class FriendsComparator : DiffUtil.ItemCallback<FriendListItem>() {
        override fun areItemsTheSame(oldItem: FriendListItem, newItem: FriendListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FriendListItem, newItem: FriendListItem): Boolean {
            return oldItem.username == newItem.username
        }
    }
}