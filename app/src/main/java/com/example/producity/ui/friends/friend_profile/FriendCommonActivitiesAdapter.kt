package com.example.producity.ui.friends.friend_profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.models.Activity
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropSquareTransformation

class FriendCommonActivitiesAdapter(private val context: Fragment) :
    ListAdapter<Activity, FriendCommonActivitiesAdapter.ActivityViewHolder>(ActivityComparator()) {

    class ActivityViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.my_activity_card_image)
        val title: TextView = view.findViewById(R.id.my_activity_card_title)
        val time: TextView = view.findViewById(R.id.my_activity_card_time)
        val date: TextView = view.findViewById(R.id.my_activity_card_date)

        fun bind(imageUrl: String, text1: String?, text2: String?, text3: String?) {
            Picasso.get().load(imageUrl).transform(CropSquareTransformation()).into(image)
            title.text = text1
            time.text = text2
            date.text = text3
        }

        companion object {
            fun create(parent: ViewGroup): ActivityViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.my_activity_view_item, parent, false)
                return ActivityViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current.imageUrl, current.title, current.date.time.toString(), current.date.date.toString())

        holder.itemView.setOnClickListener {
//            val action = MyActivityFragmentDirections.actionNavigationHomeToScheduleDetailFragment(
//                current.title,
//                current.time
//            )
//            context.findNavController().navigate(action)
        }

    }


    class ActivityComparator : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(
            oldItem: Activity,
            newItem: Activity
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Activity,
            newItem: Activity
        ): Boolean {
            return oldItem.title == newItem.title && oldItem.owner == newItem.owner
        }
    }
}