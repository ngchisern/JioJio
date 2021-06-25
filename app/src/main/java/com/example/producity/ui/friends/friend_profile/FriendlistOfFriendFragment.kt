package com.example.producity.ui.friends.friend_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentFriendlistOfFriendBinding
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [FriendlistOfFriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendlistOfFriendFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val friendlistOfFriendViewModel: FriendlistOfFriendViewModel by activityViewModels {
        FriendlistOfFriendViewModelFactory(ServiceLocator.provideFriendlistOfFriendRepository())
    }
    private val friendListViewModel: FriendListViewModel by activityViewModels {
        FriendListViewModelFactory(ServiceLocator.provideFriendListRepository())
    }

    private var _binding: FragmentFriendlistOfFriendBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFriendlistOfFriendBinding.inflate(inflater, container, false)
        val root = binding.root

        // back button
        val toolbar = binding.topAppBar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.friendlistOfFriendRecyclerView
        val adapter = FriendlistOfFriendAdapter(this, friendListViewModel, sharedViewModel)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        friendlistOfFriendViewModel.getAllFriends().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}