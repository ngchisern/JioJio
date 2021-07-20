package com.example.producity.ui.profile.memory

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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MemoryAdapter(val context: Fragment, val memoryViewModel: MemoryViewModel) :
    ListAdapter<Activity, MemoryAdapter.MemoryViewHolder>(MemoryComparator()) {

    class MemoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.activity_card_image)
        val title: TextView = view.findViewById(R.id.activity_card_title)
        val date: TextView = view.findViewById(R.id.activity_card_date)


        fun bind(current: Activity) {
            val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

            //Picasso.get().load(current.imageUrl).into(image)
            title.text = current.title
            date.text = dateFormat.format(current.date)
        }

        companion object {
            fun create(parent: ViewGroup): MemoryViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.memories_view_item, parent, false)
                return MemoryViewHolder(itemView)
            }
        }
    }


    class MemoryComparator : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.title == newItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        return MemoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        memoryViewModel.loadActivityImage(current.docId, holder.image)
    }

}