package com.example.producity.ui.friends.friend_profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentFriendProfileBinding
import com.example.producity.ui.friends.ParcelableUser
import com.example.producity.models.User
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.lang.Exception

private const val FRIEND_PROFILE = "friendProfile"


class FriendProfileFragment : Fragment() {

    private lateinit var parcelableFriend: ParcelableUser
    private lateinit var friend: User

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val friendlistOfFriendViewModel: FriendlistOfFriendViewModel by activityViewModels {
        FriendlistOfFriendViewModelFactory(ServiceLocator.provideUserRepository())
    }

    private var _binding: FragmentFriendProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parcelableFriend = it.getParcelable(FRIEND_PROFILE)!!
        }
        friend = User(
            parcelableFriend.username, parcelableFriend.uid, parcelableFriend.displayName,
            parcelableFriend.telegramHandle, parcelableFriend.gender, parcelableFriend.birthday,
            parcelableFriend.bio, parcelableFriend.imageUrl
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFriendProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // back button
        val toolbar = binding.topAppBar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


        toolbar.title = friend.username

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFriendProfile()

        // Open Telegram
        binding.telegramLogoButton.setOnClickListener {
            if (friend.telegramHandle == "") {
                Toast.makeText(context, "The user has not set a Telegram handle", Toast.LENGTH_LONG)
                    .show()
                Log.d("FriendProfileFragment", "No Telegram handle")
            } else {
                launchTelegram()
            }
        }

        // Open friends list
        binding.allFriendsButton.setOnClickListener {
            // Pass current friend username to friendlistOfFriendViewModel and load their friend list
            friendlistOfFriendViewModel.loadFriendsFromDB(friend.username)

            val action =
                FriendProfileFragmentDirections.actionFriendProfileFragmentToFriendlistOfFriendFragment()
            findNavController().navigate(action)
        }


//        // Add the RecyclerView (Upcoming activities) //TODO
//        val recyclerView = binding.friendActivitiesRecyclerView
//        val adapter = FriendCommonActivitiesAdapter(this)
//        recyclerView.adapter = adapter
//        recyclerView.addItemDecoration(
//            DividerItemDecoration(
//                context,
//                DividerItemDecoration.VERTICAL
//            )
//        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadFriendProfile() {
        val imageView = binding.friendProfilePic
        val imageUrl = friend.imageUrl
        Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(imageView)

        binding.friendDisplayName.text = friend.displayName
        binding.friendUsername.text = friend.username
        binding.friendTelegramHandle.text = friend.telegramHandle
        binding.friendGender.text = friend.gender
        binding.friendBirthday.text = friend.birthday
        binding.friendBio.text = friend.bio
    }

    private fun launchTelegram() {
        val telegramHandle = friend.telegramHandle
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://telegram.me/$telegramHandle")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}