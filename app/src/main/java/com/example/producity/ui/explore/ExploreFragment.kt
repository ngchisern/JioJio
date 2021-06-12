package com.example.producity.ui.explore

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.producity.R
import com.example.producity.databinding.FragmentExploreBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate
import java.util.*

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val exploreViewModel: ExploreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Add the RecyclerView
        val recyclerView = binding.taskRecyclerView
        val adapter = ExploreListAdapter(this, exploreViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        // Add an observer on the LiveData returned by getTasks.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        exploreViewModel.friendActivities.observe(owner = this.viewLifecycleOwner) { tasks ->
            // Update the cached copy of the tasks in the adapter.
            tasks.let { adapter.submitList(it) }
        }

        _binding!!.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search_bar -> {
                    true
                }
                R.id.filter -> {
                    true
                }
                R.id.friends_or_public -> {
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNewTask() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view: View = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_task_dialog, null)

        val datePickerButton = view.findViewById<ImageButton>(R.id.task_date_picker_button)
        datePickerButton.setOnClickListener {
            showDatePickerDialog(view)
        }

        builder
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val text1: EditText = view.findViewById(R.id.task_input_1)
                val taskName = text1.text.toString()
                val text2: EditText = view.findViewById(R.id.task_input_3)
                val description = text2.text.toString()

                /*
                taskViewModel.insert(
                    Task(taskName, LocalDate.now(), taskViewModel.newTaskDeadline, description)
                )

                 */
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    class DatePickerFragment(private val prevView: View, private val exploreViewModel: ExploreViewModel) :
        DialogFragment(),
        DatePickerDialog.OnDateSetListener {

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
            exploreViewModel.newTaskDeadline = LocalDate.of(year, month + 1, day)

            // set the text beside the datePickerButton to show the date
            val dateText = prevView.findViewById<TextView>(R.id.task_input_2)
            dateText.text = exploreViewModel.newTaskDeadline.toString()
        }
    }

    private fun showDatePickerDialog(view: View) {
        val datePickerFragment = DatePickerFragment(view, exploreViewModel)
        datePickerFragment.show(requireFragmentManager(), "datePicker")
    }

}