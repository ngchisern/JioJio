package com.example.producity.ui.profile.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.databinding.FragmentFriendListBinding
import com.example.producity.databinding.NotificationBinding
import com.example.producity.ui.friends.my_friends.FriendListAdapter
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel

class NotificationFragment: Fragment() {

    private var _binding: NotificationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val notificationViewModel: NotificationViewModel by viewModels()
    private val friendListViewModel: FriendListViewModel by activityViewModels()
    private val myActivityViewModel: MyActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val floatingButton: View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = false

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        listen()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationViewModel.updateList(friendListViewModel.currentUser.value!!.username)

        val recyclerView = binding.notificationRecycleView
        val adapter = NotificationAdapter(this, myActivityViewModel)
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        notificationViewModel.notificationList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        binding.topAppBar.setNavigationOnClickListener {
            val action = NotificationFragmentDirections.actionNotificationFragmentToNavigationProfile()
            findNavController().navigate(action)
        }
    }
}