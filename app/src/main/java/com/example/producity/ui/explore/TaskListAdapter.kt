package com.example.producity.ui.explore

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

class TaskListAdapter(private val context: Fragment, private val taskViewModel: TaskViewModel) :
    ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_view_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)

        holder.taskTextView.text = current.task
        holder.taskDeadline.text = current.deadline.toString()

        holder.doneButton.setOnClickListener {

        }

        holder.deleteButton.setOnClickListener {

        }

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

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTextView: TextView = itemView.findViewById(R.id.task_text_view)
        val taskDeadline: TextView = itemView.findViewById(R.id.task_deadline)
        val doneButton: ImageButton = itemView.findViewById(R.id.done_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    companion object {
        private val TASKS_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.task == newItem.task
            }
        }
    }

}