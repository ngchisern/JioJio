package com.example.producity.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso

class NotificationAdapter(
    val context: Fragment, val myActivityViewModel: MyActivityViewModel,
    val sharedViewModel: SharedViewModel, val notificationViewModel: NotificationViewModel
) : ListAdapter<Any, NotificationAdapter.NotificationViewHolder>(NotificationComparator()) {

    class NotificationViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup): NotificationViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.notification_viewpager, parent, false)


                return NotificationViewHolder(itemView)
            }
        }
    }

    class NotificationComparator : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return false
        }

    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val view = holder.itemView
        val username = sharedViewModel.getUser().username

        if (position == 0) {
            val recyclerView: RecyclerView = view.findViewById(R.id.notification_recycle_view)
            val adapter = SocialUpdateAdapter(
                context,
                myActivityViewModel,
                sharedViewModel,
                notificationViewModel
            )
            recyclerView.adapter = adapter

            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    view.context,
                    DividerItemDecoration.VERTICAL
                )
            )

            notificationViewModel.getUpdate(username).observe(context.viewLifecycleOwner) {
                val image = context.requireView().findViewById<ImageView>(R.id.empty_noti_image)
                val text = context.requireView().findViewById<TextView>(R.id.empty_noti_text)

                first = it.size

                if (it.isEmpty()) {
                    Picasso.get()
                        .load("https://cdn.dribbble.com/users/1418633/screenshots/6693173/empty-state.png")
                        .into(image)

                    text.text = "No Social Update"

                } else {
                    image.setImageDrawable(null)
                    text.text = ""
                }

                adapter.submitList(it)
            }

        } else {
            val recyclerView: RecyclerView = view.findViewById(R.id.notification_recycle_view)
            val adapter = NotiRequestAdapter(
                context,
                myActivityViewModel,
                sharedViewModel,
                notificationViewModel
            )
            recyclerView.adapter = adapter

            notificationViewModel.getRequest(username).observe(context.viewLifecycleOwner) {
                val image = context.requireView().findViewById<ImageView>(R.id.empty_noti_image)
                val text = context.requireView().findViewById<TextView>(R.id.empty_noti_text)

                second = it.size

                if (it.isEmpty()) {
                    Picasso.get()
                        .load("https://cdn.dribbble.com/users/1418633/screenshots/6693173/empty-state.png")
                        .into(image)

                    text.text = "No Recent Requests"

                } else {
                    image.setImageDrawable(null)
                    text.text = ""
                }

                adapter.submitList(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.create(parent)
    }

    private var first: Int? = null
    private var second: Int? = null

    fun getListSize(position: Int): Int {
        return if (position == 0) {
            first ?: 0
        } else {
            second ?: 0
        }
    }


}