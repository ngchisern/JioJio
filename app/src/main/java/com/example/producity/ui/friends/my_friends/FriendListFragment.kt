package com.example.producity.ui.friends.my_friends

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentFriendListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendListFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val friendListViewModel: FriendListViewModel by activityViewModels {
        FriendListViewModelFactory(
            ServiceLocator.provideUserRepository(),
            ServiceLocator.provideActivityRepository()
        )
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

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.friendRecycleView
        val adapter = FriendListAdapter(this, friendListViewModel, sharedViewModel)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val currentUsername = sharedViewModel.currentUser.value!!.username
        friendListViewModel.getAllFriends(currentUsername).observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                Picasso.get()
                    .load("https://thumbs.dreamstime.com/b/happy-cat-dog-friendship-cartoon-illustration-best-friends-memes-68796146.jpg")
                    .into(binding.emptyFriendlistImage)

                val addFriend = "Get started by adding a friend"
                binding.emptyFriendlistText.text = addFriend
            }

            adapter.submitList(it)
        }

        listen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_friend -> {
                    searchUser()
                    true
                }
                else -> false
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun searchUser() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.searchfriend_dialog, null)

        builder.setView(view)

        val editText: EditText = view.findViewById(R.id.searchfriend_input)

        val dialog = builder.create()
        val error: TextView = view.findViewById(R.id.searchfriend_errormessage)

        dialog.show()

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                checkUser(editText.text.toString(), dialog, error)
            }
            true
        }

        editText.doOnTextChanged { _, _, _, _ ->
            error.textSize = 0F
        }

        editText.isFocusableInTouchMode = true
        editText.requestFocus()

        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

    }


    private fun checkUser(username: String, dialog: Dialog, textView: TextView) {
        CoroutineScope(Dispatchers.Main).launch {
            val user = friendListViewModel.checkUserExists(username)
            if (user != null) {
                //friendListViewModel.sendFriendRequest(sharedViewModel.currentUser.value!!, checkUsername)
                val action =
                    FriendListFragmentDirections.actionFriendListFragmentToFriendProfileFragment(
                        user
                    )
                findNavController().navigate(action)
                dialog.cancel()
            } else {
                textView.textSize = 12F
            }
        }
    }

}