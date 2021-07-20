package com.example.producity.ui.myactivity

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

class PagerAdapter(private val context: Fragment, val myActivityViewModel: MyActivityViewModel) :
    ListAdapter<Activity, PagerAdapter.PagerViewHolder>(
        PagerComparator()
    ) {

    class PagerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.pageritem_image)
        val title: TextView = view.findViewById(R.id.pageritem_title)

        fun bind(current: Activity) {
            title.text = current.title
        }

        companion object {
            fun create(parent: ViewGroup): PagerViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_pager_viewitem, parent, false)
                return PagerViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder.create(parent)
    }


    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener {
            val action =
                MyActivityFragmentDirections.actionNavigationHomeToScheduleDetailFragment(current)
            context.findNavController().navigate(action)
        }

        myActivityViewModel.loadImage(current.docId, holder.image)

    }

    class PagerComparator : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.title == newItem.title
        }
    }


}