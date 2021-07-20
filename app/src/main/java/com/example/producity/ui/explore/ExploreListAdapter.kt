package com.example.producity.ui.explore

import android.location.Geocoder
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
import com.google.android.material.card.MaterialCardView
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

        holder.title.text = current.title
        holder.owner.text = current.owner
        holder.month.text = monthFormat.format(current.date)
        holder.day.text = dayFormat.format(current.date)

        if(current.isVirtual) {
            holder.location.text = "Online"
        } else {
            val geocoder = Geocoder(context.requireContext(), Locale.getDefault())

            val address = geocoder.getFromLocation(current.latitude, current.longitude, 1)

            if(address.isEmpty()) {
                holder.location.text = "Unknown"
            } else {
                holder.location.text = address[0].getAddressLine(0)
            }

        }

        holder.itemView.findViewById<MaterialCardView>(R.id.my_activity_card).setOnClickListener {
            val action = ExploreFragmentDirections.actionNavigationExploreToExploreDetailFragment(current)
            context.findNavController().navigate(action)
        }

        exploreViewModel.loadActivityImage(current.docId, holder.imageView)
        exploreViewModel.loadUserImage(current.owner, holder.ownerImage)

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