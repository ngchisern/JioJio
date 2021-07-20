package com.example.producity.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel
import java.util.*

class NotificationAdapter(val context: Fragment, val myActivityViewModel: MyActivityViewModel,
                          val sharedViewModel: SharedViewModel, val notificationViewModel: NotificationViewModel)
    : ListAdapter<Any, NotificationAdapter.NotificationViewHolder>(NotificationComparator()){

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

        if(position == 0 ) {
            val recyclerView: RecyclerView = view.findViewById(R.id.notification_recycle_view)
            val adapter = SocialUpdateAdapter(context, myActivityViewModel, sharedViewModel, notificationViewModel)
            recyclerView.adapter = adapter

            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    view.context,
                    DividerItemDecoration.VERTICAL
                )
            )

            notificationViewModel.getUpdate(username).observe(context.viewLifecycleOwner) {
                adapter.submitList(it)
            }

        } else {
            val recyclerView: RecyclerView = view.findViewById(R.id.notification_recycle_view)
            val adapter = NotiRequestAdapter(context, myActivityViewModel, sharedViewModel, notificationViewModel)
            recyclerView.adapter = adapter

            notificationViewModel.getRequest(username).observe(context.viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.create(parent)
    }

    private val first: List<Any>? = null
    private val second: List<Any>? = null

    fun getListSize(position: Int): Int {
        return if(position == 0) {
            first?.size ?: 0
        } else {
            second?.size ?: 0
        }
    }



}