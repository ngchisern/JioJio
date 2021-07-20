package com.example.producity.ui.myactivity.myactivitydetail.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.ChatRoomBinding
import com.example.producity.models.Message
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.firebase.Timestamp

class ChatFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by activityViewModels()

    private var _binding: ChatRoomBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ChatRoomBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val event = ChatFragmentArgs.fromBundle(requireArguments()).event

        val username = sharedViewModel.currentUser.value!!.username

        chatViewModel.documentId = event.docId

        chatViewModel.updateList()

        val recyclerView = binding.recyclerChat
        val adapter = ChatAdapter(this, username, chatViewModel)

        recyclerView.adapter = adapter

        chatViewModel.chatMessages.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }

        updateLayout()
        listen()
    }

    override fun onDestroyView() {
        val username = sharedViewModel.currentUser.value!!.username
        chatViewModel.updateReadMessage(username)
        super.onDestroyView()
        _binding = null
    }


    private fun updateLayout() {
        val event = ChatFragmentArgs.fromBundle(requireArguments()).event

        binding.groupText.text = event.title
        chatViewModel.loadActivityImage(event.docId, binding.groupImage)
    }

    private fun listen() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonChatSend.setOnClickListener {
            val msg = binding.editChatMessage.text.toString()
            val user = sharedViewModel.currentUser.value!!

            val message = Message(msg, user.username, Timestamp.now().toDate().time)
            chatViewModel.sendMessage(message)

            binding.editChatMessage.apply {
                clearFocus()
                text.clear()
            }

        }

        binding.groupImage.setOnClickListener {
            navigateToEvent()
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.more -> {
                    navigateToEvent()
                    true
                }
                else -> true
            }
        }

    }

    private fun navigateToEvent() {
        val event = ChatFragmentArgs.fromBundle(requireArguments()).event
        val action =
            ChatFragmentDirections.actionMyActivityLogFragmentToScheduleDetailFragment(event)
        findNavController().navigate(action)
    }


}