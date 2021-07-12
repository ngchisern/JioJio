package com.example.producity.ui.profile.memory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.LoginActivity
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentHomeBinding
import com.example.producity.databinding.MemoriesBinding
import com.example.producity.ui.myactivity.MyActivityAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.ktx.Firebase

class MemoryFragment: Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val memoryViewModel: MemoryViewModel by viewModels() {
        MemoryViewModelFactory(ServiceLocator.provideActivityRepository())
    }

    private var _binding: MemoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //_binding = FragmentHomeBinding.inflate(inflater, container, false)
        _binding = MemoriesBinding.inflate(inflater, container, false)

        val root: View = binding.root


        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.memoryRecycleView
        val adapter = MemoryAdapter(this)

        recyclerView.adapter = adapter

        memoryViewModel.getList(sharedViewModel.getUser().username).observe(viewLifecycleOwner) {
            if(it.isEmpty()) return@observe

            adapter.submitList(it)
        }

        listen()
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