package com.example.producity.ui.dashboard

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.producity.MyApplication
import com.example.producity.R
import com.example.producity.databinding.FragmentDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((requireActivity().application as MyApplication).taskRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Add the RecyclerView
        val recyclerView = binding.taskRecyclerView
        val adapter = TaskListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        // Add an observer on the LiveData returned by getTasks.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        taskViewModel.allTasks.observe(owner = this.viewLifecycleOwner) { tasks ->
            // Update the cached copy of the tasks in the adapter.
            tasks.let { adapter.submitList(it) }
        }

        _binding!!.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_task_item -> {
                    getNewTask()
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

    private fun getNewTask() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view: View = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_task_dialog, null)

        val datePickerButton = view.findViewById<ImageButton>(R.id.taskDatePickerButton)
        datePickerButton.setOnClickListener {
            showDatePickerDialog(view)
        }

        builder.setTitle(getString(R.string.new_task))
            .setView(view)
            .setPositiveButton("Done") { _, _ ->
                val text1: EditText = view.findViewById(R.id.taskInput1)
                val taskName = text1.text.toString()
                val text2: TextView = view.findViewById(R.id.taskInput2)
                val deadline = text2.text.toString()
                taskViewModel.insert(Task(taskName, deadline))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    class DatePickerFragment(private val prevView: View) : DialogFragment(),
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

        //@RequiresApi(Build.VERSION_CODES.O)
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val c = Calendar.getInstance()
            c.set(year, month, day)
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dateString: String = formatter.format(c.time)

            // set the text beside the datePickerButton to show the date
            val dateText = prevView.findViewById<TextView>(R.id.taskInput2)
            dateText.text = dateString
        }
    }

    private fun showDatePickerDialog(view: View) {
        val datePickerFragment = DatePickerFragment(view)
        datePickerFragment.show(requireFragmentManager(), "datePicker")
    }

}