package com.example.producity.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R

class HomeAdapter (
    private val context: Fragment,
    private val dataset: List<ScheduleDetail>
        ): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {


    class HomeViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.schedule_time)
        val title: TextView = view.findViewById(R.id.schedule_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_view_item, parent, false)

        return HomeViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = dataset[position]
        holder.time.text = item.a
        holder.title.text = item.b

        holder.itemView.setOnClickListener {
            context.findNavController().navigate(R.id.action_navigation_home_to_schedule_Detail)
        }
    }

    override fun getItemCount(): Int = dataset.size
}