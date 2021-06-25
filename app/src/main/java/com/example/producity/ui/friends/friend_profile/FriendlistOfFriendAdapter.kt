package com.example.producity.ui.friends.friend_profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.models.ParcelableUser
import com.example.producity.models.User
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class FriendlistOfFriendAdapter(
    private val context: Fragment,
    private val friendListViewModel: FriendListViewModel,
    private val sharedViewModel: SharedViewModel
) :
    ListAdapter<User, FriendlistOfFriendAdapter.FriendsViewHolder>(FriendsComparator()) {

    class FriendsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friend_username)
        val pic: ImageView = view.findViewById(R.id.friend_image)

        fun bind(text: String?, imageUrl: String) {
            username.text = text
            Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(pic)
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
        holder.bind(current.username, current.imageUrl)

        holder.itemView.setOnClickListener {
            val nextUser = ParcelableUser(
                current.username, current.uid, current.displayName,
                current.telegramHandle, current.gender, current.birthday,
                current.bio, current.imageUrl
            )

            if (nextUser.username == sharedViewModel.currentUser.value!!.username) { // go to own profile
                val action =
                    FriendlistOfFriendFragmentDirections.actionFriendlistOfFriendFragmentToNavigationProfile()
                context.findNavController().navigate(action)
            } else if (isFriend(nextUser.username)) { // go to FriendProfileFragment
                val action =
                    FriendlistOfFriendFragmentDirections.actionFriendlistOfFriendFragmentToFriendProfileFragment(
                        nextUser
                    )
                context.findNavController().navigate(action)
            } else { // go to StrangerProfileFragment
                val action =
                    FriendlistOfFriendFragmentDirections.actionFriendlistOfFriendFragmentToStrangerProfileFragment(
                        nextUser
                    )
                context.findNavController().navigate(action)
            }
        }
    }

    private fun isFriend(username: String): Boolean {
        val friendUsernames: List<String> =
            friendListViewModel.getAllFriends().value!!.map { it.username }
        return friendUsernames.contains(username)
    }


    class FriendsComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }
    }

}