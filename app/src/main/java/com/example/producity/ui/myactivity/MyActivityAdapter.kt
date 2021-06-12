package com.example.producity.ui.myactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropSquareTransformation
import java.net.URL

class MyActivityAdapter(val context: Fragment, val myActivityViewModel: MyActivityViewModel) :
    ListAdapter<MyActivityListItem, MyActivityAdapter.HomeViewHolder>(ScheduleComparator()) {

    class HomeViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val image : ImageView = view.findViewById(R.id.my_activity_card_image)
        val title: TextView = view.findViewById(R.id.my_activity_card_title)
        val time: TextView = view.findViewById(R.id.my_activity_card_time)
        val pax: TextView = view.findViewById(R.id.participants)

        fun bind(imageUrl: String, text1: String?, text2: String?, text3: Int? ) {
            Picasso.get().load(imageUrl).transform(CropSquareTransformation()).into(image)
            title.text = text1
            time.text = text2
            val temp = "1/${text3.toString()}"
            pax.text = temp
        }

        companion object {
            fun create(parent: ViewGroup): HomeViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.my_activity_view_item, parent, false)
                return HomeViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current.imageUrl, current.title, current.time, current.pax)

        holder.itemView.setOnClickListener {
            val action = MyActivityFragmentDirections.actionNavigationHomeToScheduleDetailFragment(
                current.title,
                current.time
            )
            context.findNavController().navigate(action)
        }

    }

    class ScheduleComparator : DiffUtil.ItemCallback<MyActivityListItem>() {
        override fun areItemsTheSame(oldItem: MyActivityListItem, newItem: MyActivityListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MyActivityListItem, newItem: MyActivityListItem): Boolean {
            return oldItem.title == newItem.title
        }
    }

}