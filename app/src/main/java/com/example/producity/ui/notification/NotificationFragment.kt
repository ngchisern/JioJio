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
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.NotificationBinding
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso

class NotificationFragment : Fragment() {

    private var _binding: NotificationBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val notificationViewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory(ServiceLocator.provideUserRepository())
    }
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.notiPager
        val pagerAdapter =
            NotificationAdapter(this, myActivityViewModel, sharedViewModel, notificationViewModel)
        viewPager.adapter = pagerAdapter

        val tabLayout = binding.tabLayout

        pagerAdapter.submitList(listOf(1, 2))

        val tabTitles = arrayOf("Social Update", "Request")

        TabLayoutMediator(tabLayout, viewPager) { myTabLayout, position ->
            myTabLayout.text = tabTitles[position]

            val size = pagerAdapter.getListSize(0) ?: return@TabLayoutMediator

            if (size <= 0) {
                Picasso.get()
                    .load("https://cdn.dribbble.com/users/1418633/screenshots/6693173/empty-state.png")
                    .into(binding.emptyNotiImage)
                binding.emptyNotiText.text = "No Social Update"

            }

        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position
                val size = pagerAdapter.getListSize(position) ?: return

                if (size <= 0) {
                    Picasso.get()
                        .load("https://cdn.dribbble.com/users/1418633/screenshots/6693173/empty-state.png")
                        .into(binding.emptyNotiImage)
                    if (position == 0) {
                        binding.emptyNotiText.text = "No Social Update"
                    } else {
                        binding.emptyNotiText.text = "No Recent Requests"
                    }
                } else {
                    binding.emptyNotiImage.setImageDrawable(null)
                    binding.emptyNotiText.text = ""
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

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