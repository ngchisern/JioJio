package com.example.producity.ui.friends.my_friends

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
import com.example.producity.databinding.FragmentFriendListBinding
import com.example.producity.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendListFragment : Fragment() {

    private val friendListViewModel: FriendListViewModel by activityViewModels()

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

        friendListViewModel.allFriends.observe(viewLifecycleOwner) {
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
            .setPositiveButton("Add") { dialog, which ->
                performAddFriend(input.text.toString())
            }
            .setNeutralButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

        builder.show()

    }

    private fun performAddFriend(username: String) {
        Log.d("Main", "$username")
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser ?: return

        db.document("users/$username")
            .get()
            .addOnSuccessListener {
                if (it == null || !it.exists()) {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                    Log.d("Main", "No doc")
                    return@addOnSuccessListener
                }

                val friend = it.toObject(User::class.java)
                val currentUserName = friendListViewModel.currentUser.value?.username

                Log.d("Main", "$currentUserName")

                if (currentUserName == null || friend == null) {
                    Log.d("Main", "$currentUserName $friend")
                    return@addOnSuccessListener
                }

                db.document("users/$currentUserName/friends/$username")
                    .set(friend)

                db.document("users/$username/friends/$currentUserName")
                    .set(friendListViewModel.currentUser.value!!)

                Log.d("Main", "added")
                friendListViewModel.addFriend(friend)
            }
            .addOnFailureListener {
                Log.d("Main", it.toString())
            }
    }
}