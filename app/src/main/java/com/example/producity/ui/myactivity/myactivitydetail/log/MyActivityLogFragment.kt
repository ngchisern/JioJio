package com.example.producity.ui.myactivity.myactivitydetail.log

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.ActivityDetailLogBinding
import com.example.producity.models.Activity
import com.example.producity.models.ActivityLog
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.example.producity.ui.myactivity.myactivitydetail.MyActivityDetailFragmentArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MyActivityLogFragment: Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val myActivityLogViewModel: MyActivityLogViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by activityViewModels()

    private var _binding: ActivityDetailLogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ActivityDetailLogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val floatingButton: View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = false

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position =MyActivityDetailFragmentArgs.fromBundle(requireArguments()).position

        val activity: Activity
        if(myActivityViewModel.isUpcoming) {
            activity = myActivityViewModel.myActivityList.value!!.get(position)
            myActivityLogViewModel.updateList(activity.docId)
        } else {
            activity = myActivityViewModel.pastActivityList.value!!.get(position)
            myActivityLogViewModel.updateList(activity.docId)
        }

        val preparationView = binding.activityPreparationRecyclerview
        val happeningView = binding.activityHappeningRecyclerview
        val closureView = binding.activityClosureRecyclerview

        val pAdapter = MyActivityLogAdapter(this)
        val hAdapter = MyActivityLogAdapter(this)
        val cAdapter = MyActivityLogAdapter(this)

        preparationView.apply {
            adapter = pAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        happeningView.apply {
            adapter = hAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        closureView.apply {
            adapter = cAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        myActivityLogViewModel.activityLog.observe(viewLifecycleOwner, { t ->
            t.let {
                pAdapter.submitList(it.filter { x->
                    x.stage == 0
                })

                hAdapter.submitList(it.filter { x->
                    x.stage == 1
                })

                cAdapter.submitList(it.filter { x->
                    x.stage == 2
                })
            }
        }
        )

        listen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        val position =MyActivityDetailFragmentArgs.fromBundle(requireArguments()).position
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.add_log -> {
                    addLog(position)
                    true
                }
                else -> true
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            val action = MyActivityLogFragmentDirections.actionMyActivityLogFragmentToScheduleDetailFragment(position)
            findNavController().navigate(action)
        }
    }

    private fun addLog(position: Int) {
        val builder = MaterialAlertDialogBuilder(requireActivity())

        val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.activity_add_log, null)

        builder.setView(view)

        builder.setPositiveButton("Okay", DialogInterface.OnClickListener { dialog, which ->
            val documentId: String

            if(myActivityViewModel.isUpcoming) {
                documentId = myActivityViewModel.myActivityList.value!![position].docId
            } else {
                documentId = myActivityViewModel.pastActivityList.value!![position].docId
            }

            addToDatabase(documentId, view)

        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })

        builder.show()

    }

    private fun addToDatabase(documentId: String, view: View) {
        val subject: EditText = view.findViewById(R.id.add_log_subject)
        val content: EditText = view.findViewById(R.id.add_log_content)
        val mention: EditText = view.findViewById(R.id.add_log_mention)
        val radioGroup: RadioGroup = view.findViewById(R.id.log_state_toggle)

        val user = sharedViewModel.currentUser.value ?: return

        val index = radioGroup.indexOfChild(radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId))

        val newLog = ActivityLog(user.imageUrl, user.username, index, subject.text.toString(),
                            content.text.toString(), listOf() )

        val rtdb = Firebase.database


        rtdb.getReference().child("activitylog/$documentId").push()
            .setValue(newLog)
            .addOnSuccessListener {
                Log.d("Main", "added")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }


    }

}