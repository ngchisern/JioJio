package com.example.producity.ui.notification

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
import com.example.producity.SharedViewModel
import com.example.producity.databinding.NotificationBinding
import com.example.producity.ui.myactivity.MyActivityViewModel

class NotificationFragment: Fragment() {

    private var _binding: NotificationBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        listen()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationViewModel.updateList(sharedViewModel.currentUser.value!!.username)

        val recyclerView = binding.notificationRecycleView
        val adapter = NotificationAdapter(this, myActivityViewModel, sharedViewModel, notificationViewModel)
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
            findNavController().navigateUp()
        }
    }
}