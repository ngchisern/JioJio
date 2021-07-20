package com.example.producity.ui.createactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.CreateActivityBinding
import com.example.producity.databinding.CreatePrivacyBinding
import com.example.producity.models.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreatePrivacyFragment: Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val createActivityViewModel: CreateActivityViewModel by activityViewModels()

    private var _binding: CreatePrivacyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View {

        _binding = CreatePrivacyBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listen()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        binding.cancelText.setOnClickListener {
            popDiscardDialog()
        }

        binding.backIcon.setOnClickListener {
            navigateBack()
        }

        binding.privateCard.setOnClickListener {
            createActivityViewModel.updatePrivacy(Activity.PRIVATE)
            navigateToDetail()
        }

        binding.protectedCard.setOnClickListener {
            createActivityViewModel.updatePrivacy(Activity.PROTECTED)
            navigateToDetail()
        }

        binding.publicCard.setOnClickListener {
            createActivityViewModel.updatePrivacy(Activity.PUBLIC)
            navigateToDetail()
        }
    }

    private fun navigateToDetail() {
        val action = CreatePrivacyFragmentDirections.actionCreatePrivacyFragmentToCreateDetailFragment()
        findNavController().navigate(action)
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun popDiscardDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.discard_dialog, null)

        builder.setView(view)

        val discard: TextView = view.findViewById(R.id.discard_button)
        val remain: TextView = view.findViewById(R.id.remain_button)

        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        discard.setOnClickListener {
            dialog.dismiss()
            cancel()
        }

        remain.setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun cancel() {
        createActivityViewModel.cleanUp()
        findNavController().popBackStack(R.id.navigation_createActivity, false)
        findNavController().navigate(R.id.navigation_myActivity)
    }
}