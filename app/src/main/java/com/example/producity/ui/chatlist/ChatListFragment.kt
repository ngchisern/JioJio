package com.example.producity.ui.chatlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.ChatListBinding
import com.squareup.picasso.Picasso

class ChatListFragment : Fragment() {

    private var _binding: ChatListBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val chatListViewModel: ChatListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ChatListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = sharedViewModel.getUser().username

        val recyclerView = binding.recyclerChatlist
        val adapter = ChatListAdapter(this, username)

        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        chatListViewModel.chatRooms.observe(viewLifecycleOwner) {
            Log.d("Main", "observe ${it.size}")

            if (it.isEmpty()) {
                val title = "You don't have any \nchat room yet"
                binding.emptyChatlistTitle.text = title
                val subtitle = "Chat room is automatically created when you created/joined an activity."
                binding.emptyChatlistSubtitle.text = subtitle
                adapter.submitList(it)
                return@observe
            }
            binding.emptyChatlistTitle.text = ""
            binding.emptyChatlistSubtitle.text = ""
            adapter.submitList(it)
        }


        updateLayout()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateLayout() {
        Picasso.get().load(sharedViewModel.userImage.value!!).into(binding.profileImage)
    }


}