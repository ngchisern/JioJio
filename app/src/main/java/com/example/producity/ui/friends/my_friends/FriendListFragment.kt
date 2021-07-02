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
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentFriendListBinding
import com.example.producity.models.Notification
import com.example.producity.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

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
                    popAddFriendDialog()
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

    private fun popAddFriendDialog() {
        val builder = context?.let { MaterialAlertDialogBuilder(it) }

        if (builder == null) return

        val input = EditText(context)
        input.setHint("username")
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setTitle("Add friends")
            .setView(input)
            .setPositiveButton("Add", null)
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
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser ?: return

        db.document("users/${input.text}")
            .get()
            .addOnSuccessListener {
                if (it == null || !it.exists()) {
                    input.setError("User does not exist")
                    Log.d("Main", "No doc")
                    return@addOnSuccessListener
                }

                val friend = it.toObject(User::class.java) ?: return@addOnSuccessListener

                sendFriendRequest(friend)

            }
            .addOnFailureListener {
                Log.d("Main", it.toString())
            }
    }

    private fun sendFriendRequest(user: User) {
        val sender = sharedViewModel.currentUser.value ?: return

        val rtdb = Firebase.database

        val noti = Notification(sender.imageUrl,
            "${sender.username} sent you a friend request",
            true,
            null,
            Timestamp.now().toDate().time,
            sender.username)

        rtdb.getReference().child("notification/${user.username}").push()
            .setValue(noti)
            .addOnSuccessListener {
                Log.d("Main", "added noti")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }

    }


}