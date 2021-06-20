package com.example.producity.ui.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.models.Activity
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ExploreListAdapter(private val context: Fragment, private val exploreViewModel: ExploreViewModel) :
    ListAdapter<Activity, ExploreListAdapter.ExploreViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_view_item, parent, false)
        return ExploreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        val current = getItem(position)

        val dayFormat: DateFormat = SimpleDateFormat("d", Locale.getDefault())
        val monthFormat: DateFormat = SimpleDateFormat("MMM", Locale.getDefault())

        Picasso.get().load(current.imageUrl).into(holder.imageView)
        Picasso.get().load(current.ownerImageUrl).into(holder.ownerImage)

        holder.title.text = current.title
        holder.owner.text = current.owner
        holder.month.text = monthFormat.format(current.date)
        holder.day.text = dayFormat.format(current.date)

        if(!current.isVirtual) {
            holder.location.text = current.location
        } else {
            holder.location.setText("Online")
        }

        holder.itemView.findViewById<MaterialCardView>(R.id.my_activity_card).setOnClickListener {
            val action = ExploreFragmentDirections.actionNavigationExploreToExploreDetailFragment(position)
            context.findNavController().navigate(action)
        }

    }

    class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.explore_view_item_image)
        val title: TextView = itemView.findViewById(R.id.explore_view_item_title)
        val month: TextView = itemView.findViewById(R.id.explore_view_item_month)
        val day: TextView = itemView.findViewById(R.id.explore_view_item_day)
        val location: TextView = itemView.findViewById(R.id.explore_view_item_location)
        val ownerImage: ImageView = itemView.findViewById(R.id.explore_view_item_owner_image)
        val owner: TextView = itemView.findViewById(R.id.explore_view_item_owner)

    }

    companion object {
        private val TASKS_COMPARATOR = object : DiffUtil.ItemCallback<Activity>() {
            override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }

}