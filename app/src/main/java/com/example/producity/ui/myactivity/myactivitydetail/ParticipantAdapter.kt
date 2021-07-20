package com.example.producity.ui.myactivity.myactivitydetail

import android.util.Log
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
import com.example.producity.models.Participant

class ParticipantlAdapter(val context: Fragment, val showName: Boolean, val manage: Boolean, val viewModel: MyActivityDetailViewModel)
    : ListAdapter<Participant, ParticipantlAdapter.ParticipantViewHolder>(ParticipantComparator()){


    class ParticipantViewHolder(private val view: View, private val viewModel: MyActivityDetailViewModel)
        : RecyclerView.ViewHolder(view) {

        val image : ImageView = view.findViewById(R.id.participant_image)
        val name: TextView = view.findViewById(R.id.participant_name)
        val remove: ImageView = view.findViewById(R.id.remove_icon)

        fun bind(current: Participant, showName: Boolean, manage: Boolean) {
            if(showName) {
                name.text = current.nickname
            } else {
                name.text = ""
            }

            if(manage) {
                remove.setBackgroundResource(R.drawable.ic_cancel_24)
                remove.setOnClickListener {
                    Log.d("Main", "remove")
                    viewModel.removeParticipant(current.username)
                }
            }

        }

        companion object {
            fun create(parent: ViewGroup, viewModel: MyActivityDetailViewModel): ParticipantViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_participant_item_view, parent, false)
                return ParticipantViewHolder(itemView, viewModel)
            }
        }
    }

    class ParticipantComparator : DiffUtil.ItemCallback<Participant>() {
        override fun areItemsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Participant, newItem: Participant): Boolean {
            return oldItem.username == newItem.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder.create(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current, showName, manage)

        viewModel.loadUserImage(current.username, holder.image)
    }


}