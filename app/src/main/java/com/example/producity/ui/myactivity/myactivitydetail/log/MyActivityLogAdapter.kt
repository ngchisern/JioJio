package com.example.producity.ui.myactivity.myactivitydetail.log

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.models.ActivityLog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso

class MyActivityLogAdapter(private val context: Fragment)
    : androidx.recyclerview.widget.ListAdapter<ActivityLog, MyActivityLogAdapter.MyActivityLogViewHolder>(
    MyActivityLogAdapter.LOG_COMPARATOR){


    class MyActivityLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.log_participant_image)
        val name: TextView = itemView.findViewById(R.id.log_participant_name)
        val priorityIcon: ImageView = itemView.findViewById(R.id.priority_icon)
        val title: TextView = itemView.findViewById(R.id.log_title)
    }

    companion object {
        private val LOG_COMPARATOR = object : DiffUtil.ItemCallback<ActivityLog>() {
            override fun areItemsTheSame(oldItem: ActivityLog, newItem: ActivityLog): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ActivityLog, newItem: ActivityLog): Boolean {
                return oldItem.content == newItem.content
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyActivityLogViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.activity_detail_log_item, parent, false)
        return MyActivityLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyActivityLogViewHolder, position: Int) {
        val current = getItem(position)

        Picasso.get().load(current.opUrl).into(holder.image)
        holder.title.text = current.subject
        holder.name.text = current.op

        /*
        if(!current.priority) {
            holder.priorityIcon.isVisible = false
        }

         */

        holder.itemView.findViewById<MaterialCardView>(R.id.my_activity_log_card).setOnClickListener {
            showEntireLog(current)
        }
    }

    private fun showEntireLog(log: ActivityLog) {
        val builder = MaterialAlertDialogBuilder(context.requireActivity())

        val view: View = LayoutInflater.from(context.requireContext()).inflate(R.layout.activity_log_dialog, null)

        val subject: TextView = view.findViewById(R.id.log_dialog_subject)
        val image: ImageView = view.findViewById(R.id.op_image_dialog)
        val name: TextView = view.findViewById(R.id.op_name_dialog)
        val priority: ImageView = view.findViewById(R.id.priority_icon)
        val description: TextView = view.findViewById(R.id.log_dialog_description)

        subject.setText(log.subject)
        Picasso.get().load(log.opUrl).into(image)
        name.setText(log.op)
        description.setText(log.content)

        /*
        if(!log.priority) {
            priority.isVisible = false
        }
         */

        builder.setView(view)

        builder.setPositiveButton("Okay", DialogInterface.OnClickListener { dialog, which ->

        })

        builder.show()

    }

}