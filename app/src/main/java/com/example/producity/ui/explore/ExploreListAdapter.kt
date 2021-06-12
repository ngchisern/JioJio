package com.example.producity.ui.explore

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
import com.squareup.picasso.Picasso

class ExploreListAdapter(private val context: Fragment, private val exploreViewModel: ExploreViewModel) :
    ListAdapter<ExploreListItem, ExploreListAdapter.ExploreViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_view_item, parent, false)
        return ExploreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        val current = getItem(position)

        Picasso.get().load(current.imageUrl).into(holder.imageView)
        holder.title.text = current.title
        holder.time.text = current.time
        holder.pax.text = current.pax.toString()


        // navigate to task details fragment when clicking on the task item
//        holder.itemView.setOnClickListener {
//            val action = DashboardFragmentDirections.actionNavigationDashboardToTaskDetailFragment(
//                current.task, current.start_date.toString(),
//                current.deadline.toString(), current.description
//            )
//            context.findNavController().navigate(action)
//
//        }
    }

    class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.explore_view_item_image)
        val title: TextView = itemView.findViewById(R.id.explore_view_item_title)
        val time: TextView = itemView.findViewById(R.id.explore_view_item_time)
        val pax: TextView = itemView.findViewById(R.id.explore_view_item_pax)

        /*
        val taskTextView: TextView = itemView.findViewById(R.id.task_text_view)
        val taskDeadline: TextView = itemView.findViewById(R.id.task_deadline)
        val doneButton: ImageButton = itemView.findViewById(R.id.done_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

         */
    }

    companion object {
        private val TASKS_COMPARATOR = object : DiffUtil.ItemCallback<ExploreListItem>() {
            override fun areItemsTheSame(oldItem: ExploreListItem, newItem: ExploreListItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ExploreListItem, newItem: ExploreListItem): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }

}