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
import com.example.producity.models.Activity
import com.example.producity.ui.myactivity.MyActivityAdapter
import com.example.producity.ui.myactivity.MyActivityFragmentDirections
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FriendUpcomingActivitiesAdapter(private val context: Fragment) :
    ListAdapter<Activity, FriendUpcomingActivitiesAdapter.UpcomingActivitiesViewHolder>(
        ActivityComparator()
    ) {

    class UpcomingActivitiesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.my_activity_card_image)
        val title: TextView = view.findViewById(R.id.my_activity_card_title)
        val date: TextView = view.findViewById(R.id.my_activity_card_date)
        val time: TextView = view.findViewById(R.id.my_activity_card_time)


        fun bind(current: Activity) {
            val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

            //Picasso.get().load(current.imageUrl).into(image)
            title.text = current.title
            time.text = timeFormat.format(current.date)
            date.text = dateFormat.format(current.date)
        }

        companion object {
            fun create(parent: ViewGroup): UpcomingActivitiesViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.my_activity_view_item, parent, false)
                return UpcomingActivitiesViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingActivitiesViewHolder {
        return UpcomingActivitiesViewHolder.create(parent)
    }


    override fun onBindViewHolder(holder: UpcomingActivitiesViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

//        holder.itemView.setOnClickListener {
//            val action = FriendProfileFragmentDirections.actionFriendProfileFragmentToScheduleDetailFragment(position)
//            context.findNavController().navigate(action)
//        }
    }


    class ActivityComparator : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.title == newItem.title && oldItem.owner == newItem.owner
        }
    }
}