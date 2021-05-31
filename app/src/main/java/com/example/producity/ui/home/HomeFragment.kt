package com.example.producity.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.producity.MyApplication
import com.example.producity.R
import com.example.producity.databinding.FragmentHomeBinding
import java.util.*

class HomeFragment : Fragment() {
    private val newScheduleRequestCode = 1

    private val homeViewModel: HomeViewModel by activityViewModels() {
        HomeViewModelFactory((requireActivity().application as MyApplication).scheduleRepository)
    }

    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val floatingButton : View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = true

        val adapter = HomeAdapter(this, homeViewModel)
        _binding?.scheduleRecycleView?.adapter = adapter

        val root: View = binding.root

        val header: TextView = binding.header

        homeViewModel.header.observe(viewLifecycleOwner, {
            header.text = it
        })

        _binding?.topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorite -> {
                    popTimePicker()
                    true
                }
                else -> false
            }
        }

        homeViewModel.allScheduleDetail.observe(viewLifecycleOwner) {
            Log.d("jayson", "all")
            adapter.submitList(homeViewModel.itemlist())
        }

        homeViewModel.targetDate.observe(viewLifecycleOwner) {
            Log.d("jayson", "target")
            adapter.submitList(homeViewModel.itemlist())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val floatingButton : View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = false
        _binding = null
    }


    class DatePickerFragment(val viewModel: HomeViewModel) : DialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            return DatePickerDialog(this.requireContext(), this, year, month, day)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            viewModel.updateDate(year,month + 1,day)
        }
    }

    fun popTimePicker() {
        val newFragment = DatePickerFragment(homeViewModel)
        newFragment.show(requireFragmentManager(),"dialog")
    }

}
