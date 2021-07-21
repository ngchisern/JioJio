package com.example.producity.ui.friends.friend_profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.ReportUserBinding
import com.example.producity.models.User

const val APPSCRIPTLINK =
    "https://script.google.com/macros/s/AKfycbzDv-Znw0fbNakfo6KaVrsC6t0CFPH2JLssBjAaZ_ypptcQdNmR2-N1uD009Le4l1n4/exec"

class ReportUserFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: ReportUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ReportUserBinding.inflate(inflater, container, false)
        val root = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateLayout()
        listen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateLayout() {
        val name = ReportUserFragmentArgs.fromBundle(requireArguments()).name
        val title = "Report $name"
        binding.reportTitle.text = title


        binding.reportDescription.requestFocus()
    }


    private fun listen() {
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.submitText.setOnClickListener {
            submitReport()
        }

        binding.submitText.isClickable = false

        binding.reportDescription.doOnTextChanged { text, _, _, _ ->
            if (text == null) return@doOnTextChanged

            val submit = binding.submitText

            if (text.isEmpty()) {
                submit.isClickable = false
                submit.setTextColor(Color.GRAY)
            } else {
                submit.isClickable = true
                submit.setTextColor(Color.BLACK)

            }

        }
    }


    private fun submitReport() {

        val request = object : StringRequest(Method.POST, APPSCRIPTLINK, {
            findNavController().navigateUp()
        }, {

        }) {
            override fun getParams(): MutableMap<String, String> {
                val map: HashMap<String, String> = hashMapOf()

                val reporter = sharedViewModel.getUser().username
                val reportee = ReportUserFragmentArgs.fromBundle(requireArguments()).id
                val content = binding.reportDescription.text.toString()

                map["action"] = "addItem"
                map["reporter"] = reporter
                map["reportee"] = reportee
                map["content"] = content

                return map
            }
        }

        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }


}