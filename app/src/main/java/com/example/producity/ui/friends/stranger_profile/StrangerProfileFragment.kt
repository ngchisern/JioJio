package com.example.producity.ui.friends.stranger_profile

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentStrangerProfileBinding
import com.example.producity.models.User
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModelFactory

private const val STRANGER_PROFILE = "strangerProfile"

/**
 * A simple [Fragment] subclass.
 * Use the [StrangerProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StrangerProfileFragment : Fragment() {

    private lateinit var stranger: User

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val friendListViewModel: FriendListViewModel by activityViewModels {
        FriendListViewModelFactory(ServiceLocator.provideUserRepository(), ServiceLocator.provideActivityRepository())
    }

    private var _binding: FragmentStrangerProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stranger = it.getParcelable(STRANGER_PROFILE)!!
        }
        stranger = User(
            stranger.username,
            stranger.uid,
            stranger.nickname,
            stranger.telegramHandle,
            stranger.gender,
            stranger.birthday,
            stranger.bio
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStrangerProfileBinding.inflate(inflater, container, false)
        val root = binding.root

        // back button
        val toolbar = binding.topAppBar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        toolbar.title = stranger.username

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStrangerProfile()

        binding.strangerAddFriendButton.setOnClickListener {
            sendFriendRequest(stranger.username)
        }

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadStrangerProfile() {
        val imageView = binding.strangerProfilePic
        //Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(imageView)

        binding.strangerDisplayName.text = stranger.nickname
        binding.strangerUsername.text = stranger.username
        binding.strangerTelegramHandle.text = stranger.telegramHandle
        //binding.strangerGender.text = stranger.gender
        //binding.strangerBirthday.text = stranger.birthday
        binding.strangerBio.text = stranger.bio
    }

    private fun sendFriendRequest(username: String) {
        friendListViewModel.sendFriendRequest(sharedViewModel.currentUser.value!!, username)

        // set button to light purple and not clickable
        val button = binding.strangerAddFriendButton
        button.text = resources.getText(R.string.friend_request_sent)
        button.isEnabled = false
        button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_purple))
        button.setTextColor(resources.getColor(R.color.black))
    }

}