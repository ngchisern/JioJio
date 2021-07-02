package com.example.producity.ui.friends.my_friends

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentFriendListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

class FriendListFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val friendListViewModel: FriendListViewModel by activityViewModels {
        FriendListViewModelFactory(ServiceLocator.provideUserRepository())
    }

    private var _binding: FragmentFriendListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_friend -> {
                    popSendFriendRequestDialog()
                    true
                }
                else -> false
            }
        }

        val floatingButton: View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = false

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.friendRecycleView
        val adapter = FriendListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val currentUsername = sharedViewModel.currentUser.value!!.username
        friendListViewModel.getAllFriends(currentUsername).observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun popSendFriendRequestDialog() {
        val builder = context?.let { MaterialAlertDialogBuilder(it) }

        if (builder == null) return

        val input = EditText(context)
        input.setHint("username")
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setTitle("Send friend request")
            .setView(input)
            .setPositiveButton("Send", null)
            .setNeutralButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener {
                if(input.text.toString().isEmpty()) {
                    return@setOnClickListener
                }

                checkUser(input)
            }
        }

        dialog.show()

    }

    private fun checkUser(input: EditText) {
        CoroutineScope(Dispatchers.Main).launch {
            val checkUsername = input.text.toString()
            val exists = friendListViewModel.checkUserExists(checkUsername)
            if (exists) {
                friendListViewModel.sendFriendRequest(sharedViewModel.currentUser.value!!, checkUsername)
                Toast.makeText(context, resources.getText(R.string.friend_request_sent), Toast.LENGTH_LONG)
                    .show()
                Log.d("FriendListFragment", "Send friend request to: $checkUsername")
            } else {
                input.error = "User does not exist"
                Log.d("FriendListFragment", "checkUser: does not exist")
            }
        }
    }

}