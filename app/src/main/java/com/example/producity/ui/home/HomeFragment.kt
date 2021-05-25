package com.example.producity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // remove this
        _binding?.scheduleRecycleView?.adapter = HomeAdapter(this, listOf(
            ScheduleDetail("1100", " Done"),
            ScheduleDetail("1200", " Done"),
            ScheduleDetail("1300", " Done"),
            ScheduleDetail("1400", " Done"),
            ScheduleDetail("1500", " Done"),
            ScheduleDetail("1600", " Done"),

            ScheduleDetail("1100", " Done"),
            ScheduleDetail("1200", " Done"),
            ScheduleDetail("1300", " Done"),
            ScheduleDetail("1400", " Done"),
            ScheduleDetail("1500", " Done"),
            ScheduleDetail("1600", " Done"),

            ScheduleDetail("1100", " Done"),
            ScheduleDetail("1200", " Done"),
            ScheduleDetail("1300", " Done"),
            ScheduleDetail("1400", " Done"),
            ScheduleDetail("1500", " Done"),
            ScheduleDetail("1600", " Done"))
        )

        val root: View = binding.root

        /*
        val textView: TextView = binding.textHome


        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

         */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun navigate() {
        findNavController().navigate(R.id.action_navigation_home_to_schedule_Detail)
    }
}