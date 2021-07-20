package com.example.producity.ui.friends.my_friends

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.models.User
import java.lang.Exception

class FriendListAdapter(val context: Fragment,
                        private val friendListViewModel: FriendListViewModel,
                        private val sharedViewModel: SharedViewModel
): ListAdapter<User, FriendListAdapter.FriendsViewHolder>(FriendsComparator()) {

    class FriendsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friend_username)
        val pic: ImageView = view.findViewById(R.id.friend_image)
        val chatButton: ImageView = view.findViewById(R.id.chat_button)

        fun bind(user: User) {
            username.text = user.nickname

            if(user.telegramHandle != "") {
                chatButton.isVisible = true
                chatButton.setOnClickListener {
                    message(user)
                }
            }


        }

        private fun message(user: User) {
            if (user.telegramHandle == "") {
                Toast.makeText(view.context, "The user has not set a Telegram handle", Toast.LENGTH_LONG).show()
            } else {
                val telegramHandle = user.telegramHandle
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("http://telegram.me/$telegramHandle")
                    view.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(view.context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }




        companion object {
            fun create(parent: ViewGroup): FriendsViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.friend_list_view_item, parent, false)
                return FriendsViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        return FriendsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        friendListViewModel.loadImage(current.username, holder.pic)

        holder.itemView.setOnClickListener {
            val action = FriendListFragmentDirections.actionFriendListFragmentToFriendProfileFragment(current)
            context.findNavController().navigate(action)
        }
    }



    class FriendsComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }
    }



}