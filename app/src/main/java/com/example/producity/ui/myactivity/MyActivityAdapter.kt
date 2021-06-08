package com.example.producity.ui.myactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R

class MyActivityAdapter(val context: Fragment, val myActivityViewModel: MyActivityViewModel) :
    ListAdapter<ScheduleDetail, MyActivityAdapter.HomeViewHolder>(ScheduleComparator()) {

    class HomeViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.schedule_time)
        val title: TextView = view.findViewById(R.id.schedule_title)
        val button: ImageButton = view.findViewById(R.id.cancel_button)

        fun bind1(text: String?) {
            title.text = text
        }

        fun bind2(text: String?) {
            time.text = text
        }

        companion object {
            fun create(parent: ViewGroup): HomeViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.schedule_view_item, parent, false)
                return HomeViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind1(current.title)
        holder.bind2(current.time)

        holder.itemView.setOnClickListener {
            val action = MyActivityFragmentDirections.actionNavigationHomeToScheduleDetailFragment(
                current.title,
                current.time
            )
            context.findNavController().navigate(action)
        }

        holder.button.setOnClickListener {

        }
    }

    class ScheduleComparator : DiffUtil.ItemCallback<ScheduleDetail>() {
        override fun areItemsTheSame(oldItem: ScheduleDetail, newItem: ScheduleDetail): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ScheduleDetail, newItem: ScheduleDetail): Boolean {
            return oldItem.title == newItem.title
        }
    }

}