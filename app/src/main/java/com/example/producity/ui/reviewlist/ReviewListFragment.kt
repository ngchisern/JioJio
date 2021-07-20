package com.example.producity.ui.reviewlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.databinding.ReviewListBinding
import com.example.producity.models.User

class ReviewListFragment : Fragment() {

    private var _binding: ReviewListBinding? = null
    private val binding get() = _binding!!

    private val reviewListViewModel: ReviewListViewModel by viewModels()

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ReviewListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = ReviewListFragmentArgs.fromBundle(requireArguments()).user

        reviewListViewModel.updateList(user.username)

        val recyclerView = binding.recyclerView
        val adapter = ReviewListAdapter(this, reviewListViewModel)

        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        reviewListViewModel.reviews.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        updateLayout()
        listen()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateLayout() {
        val rating = "%.${1}f".format(user.rating / user.review).toFloat()
        binding.rating.text = rating.toString()
        binding.ratingProgress.progress = (rating / 5 * 100).toInt()
        binding.reviewStar.rating = rating
        binding.totalReview.text = "${user.review} reviews"

    }

    private fun listen() {
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}