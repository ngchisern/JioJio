package com.example.producity.ui.profile.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.models.Notification
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(val context: Fragment, val myActivityViewModel: MyActivityViewModel):
    ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationComparator()){

    class NotificationViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.sender_image)
        val message: TextView = view.findViewById(R.id.noti_message)
        val date: TextView = view.findViewById(R.id.noti_date)

        fun bind(current: Notification) {
            val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

            Picasso.get().load(current.imageUrl).transform(CropCircleTransformation()).into(image)
            message.text = current.message
            date.text = dateFormat.format(current.timestamp)
        }

        companion object {
            fun create(parent: ViewGroup): NotificationViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.notification_view_item, parent, false)
                return NotificationViewHolder(itemView)
            }
        }
    }

    class NotificationComparator : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.equals(newItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener {
            val position: Int = myActivityViewModel.documentIds.indexOf(current.docId)
            val action = NotificationFragmentDirections.actionNotificationFragmentToScheduleDetailFragment(position)
            
            context.findNavController().navigate(action)
        }
    }

}