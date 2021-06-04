package com.example.producity.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.producity.R
import com.example.producity.databinding.FragmentDashboardBinding
import com.example.producity.databinding.FragmentTaskDetailBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TaskDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskDetailFragment : Fragment() {

    private lateinit var taskName: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var description: String

    private var _binding: FragmentTaskDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskName = it.getString(TASK_NAME).toString()
            startDate = it.getString(START_DATE).toString()
            endDate = it.getString(END_DATE).toString()
            description = it.getString(DESCRIPTION).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.taskName.text = taskName
        binding.startDate.text = startDate
        binding.endDate.text = endDate
        binding.description.text = description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        // Parameter argument names
        const val TASK_NAME: String = "taskName"
        const val START_DATE: String = "startDate"
        const val END_DATE: String = "endDate"
        const val DESCRIPTION: String = "description"


        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(taskName: String, startDate: String, endDate: String, description: String) =
            TaskDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(TASK_NAME, taskName)
                    putString(START_DATE, startDate)
                    putString(END_DATE, endDate)
                    putString(DESCRIPTION, description)
                }
            }
    }
}