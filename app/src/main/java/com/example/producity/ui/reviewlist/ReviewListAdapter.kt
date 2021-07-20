package com.example.producity.ui.reviewlist

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
import com.example.producity.models.Review
import java.text.SimpleDateFormat
import java.util.*

class ReviewListAdapter(
    private val context: Fragment,
    private val reviewListViewModel: ReviewListViewModel
) :
    ListAdapter<Review, ReviewListAdapter.ReviewListViewHolder>(ReviewComparator()) {

    class ReviewListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.reviewer_image)
        val nickname: TextView = view.findViewById(R.id.reviewer_nickname)
        val content: TextView = view.findViewById(R.id.review_description)
        val time: TextView = view.findViewById(R.id.review_time)
        val rating: TextView = view.findViewById(R.id.review_rating)

        fun bind(current: Review) {
            val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            if (current.description.isEmpty()) {
                content.textSize = 0F
            } else {
                content.text = current.description
            }
            time.text = format.format(current.timestamp)
            rating.text = current.rating.toString()
        }

        companion object {
            fun create(parent: ViewGroup): ReviewListViewHolder {
                val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.review_listitem, parent, false)
                return ReviewListViewHolder(itemView)
            }
        }
    }


    class ReviewComparator : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.reviewer == oldItem.reviewer
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewListViewHolder {
        return ReviewListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        reviewListViewModel.loadImage(current.reviewer, holder.image)
        reviewListViewModel.loadNickname(current.reviewer, holder.nickname)

    }
}