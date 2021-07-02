package com.example.producity.ui.friends.stranger_profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentStrangerProfileBinding
import com.example.producity.ui.friends.ParcelableUser
import com.example.producity.models.User
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

private const val STRANGER_PROFILE = "strangerProfile"

/**
 * A simple [Fragment] subclass.
 * Use the [StrangerProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StrangerProfileFragment : Fragment() {

    private lateinit var parcelableStranger: ParcelableUser
    private lateinit var stranger: User

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val friendListViewModel: FriendListViewModel by activityViewModels {
        FriendListViewModelFactory(ServiceLocator.provideUserRepository())
    }

    private var _binding: FragmentStrangerProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parcelableStranger = it.getParcelable(STRANGER_PROFILE)!!
        }
        stranger = User(
            parcelableStranger.username, parcelableStranger.uid, parcelableStranger.displayName,
            parcelableStranger.telegramHandle, parcelableStranger.gender, parcelableStranger.birthday,
            parcelableStranger.bio, parcelableStranger.imageUrl
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
            performAddFriend(stranger.username)
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
        val imageUrl = stranger.imageUrl
        Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(imageView)

        binding.strangerDisplayName.text = stranger.displayName
        binding.strangerUsername.text = stranger.username
        binding.strangerTelegramHandle.text = stranger.telegramHandle
        binding.strangerGender.text = stranger.gender
        binding.strangerBirthday.text = stranger.birthday
        binding.strangerBio.text = stranger.bio
    }


    private fun performAddFriend(username: String) {
        Log.d("StrangerProfileFragment", username)
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser ?: return

        db.document("users/$username")
            .get()
            .addOnSuccessListener {
                if (it == null || !it.exists()) {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                    Log.d("StrangerProfileFragment", "No doc")
                    return@addOnSuccessListener
                }

                val friend = it.toObject(User::class.java)
                val currentUserName = sharedViewModel.currentUser.value?.username

                Log.d("StrangerProfileFragment", "$currentUserName")

                if (currentUserName == null || friend == null) {
                    Log.d("StrangerProfileFragment", "$currentUserName $friend")
                    return@addOnSuccessListener
                }

                db.document("users/$currentUserName/friends/$username")
                    .set(friend)

                db.document("users/$username/friends/$currentUserName")
                    .set(sharedViewModel.currentUser.value!!)

                Log.d("StrangerProfileFragment", "added")
//                friendListViewModel.addFriend(friend)

                // set button to light purple and not clickable
                val button = binding.strangerAddFriendButton
                button.text = resources.getText(R.string.added_friend)
                button.isEnabled = false
                button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_purple))
                button.setTextColor(resources.getColor(R.color.black))
            }
            .addOnFailureListener {
                Log.d("StrangerProfileFragment", it.toString())
            }
    }
}