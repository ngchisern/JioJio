package com.example.producity.ui.friends.friend_profile

import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentFriendProfileBinding
import com.example.producity.models.Request
import com.example.producity.models.User
import com.example.producity.ui.friends.my_friends.FriendListFragmentDirections
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FriendProfileFragment : Fragment() {

    private lateinit var friend: User

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val friendListViewModel: FriendListViewModel by activityViewModels {
        FriendListViewModelFactory(
            ServiceLocator.provideUserRepository(),
            ServiceLocator.provideActivityRepository()
        )
    }

    private var _binding: FragmentFriendProfileBinding? = null
    private val binding get() = _binding!!

    private var isFriend: Boolean? = null
    private var doesRequestExist: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        friend = FriendProfileFragmentArgs.fromBundle(requireArguments()).friendProfile
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFriendProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isFriend = isFriend(friend.username)
        loadFriendProfile()
        listen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        // Open Telegram
        binding.actionCard.setOnClickListener {
            if (isFriend!!) {
                message()
            } else if (!doesRequestExist!!) {
                sendFriendRequest()
                it.isClickable = false
                val sent = "Friend Request Sent"
                binding.mainAction.text = sent
                binding.mainAction.setTextColor(Color.GRAY)
            } else {
                it.isClickable = false
            }
        }

        binding.navigateBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.moreActions.setOnClickListener {
            showMoreActions()
        }

        binding.ratingLayout.setOnClickListener {
            val action =
                FriendProfileFragmentDirections.actionFriendProfileFragmentToReviewListFragment(
                    friend
                )
            findNavController().navigate(action)
        }
    }

    private fun loadFriendProfile() {

        Glide.with(requireContext()).load(friend.banner).into(binding.profileBanner)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        friendListViewModel.loadImage(friend.username, binding.profileImage)

        binding.profileNickname.text = friend.nickname
        binding.profileUsername.text = friend.username
        binding.profileGender.text = when (friend.gender) {
            0 -> "Male"
            1 -> "Female"
            2 -> "Other"
            else -> "Not yet selected"
        }
        binding.profileBirthday.text = dateFormat.format(friend.birthday)
        binding.profileBio.text = friend.bio

        viewLifecycleOwner.lifecycleScope.launch {
            doesRequestExist = exist()
            binding.mainAction.text = when {
                isFriend!! -> "Send Message"
                doesRequestExist!! -> "Friend Request Sent"
                else -> "Send Friend Request"
            }
        }

        if (friend.latitude == -1.0 && friend.longitude == -1.0) {
            val notSelected = "Not selected yet"
            binding.profileLocation.text = notSelected
        } else {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(friend.latitude, friend.longitude, 1)

            val location = "${address[0].locality}, ${address[0].countryName}"
            binding.profileLocation.text = location
        }

        if (isFriend!!) {
            binding.nextEventText.isVisible = true
            binding.nextEventCard.isVisible = true

            friendListViewModel.getNextEvent(friend.username).observe(viewLifecycleOwner) {
                if(it == null) {
                    Picasso.get().load("https://www.kindpng.com/picc/m/716-7160393_happy-people-illustration-hd-png-download.png")
                        .into(binding.profileActivityImage)
                    return@observe
                }

                friendListViewModel.loadActivityImage(it.docId, binding.profileActivityImage)
                binding.profileActivityName.text = it.title
                binding.profileActivityCountdown.text

                val countdown = binding.profileActivityCountdown

                val difference = it.date.time - Timestamp.now().toDate().time
                val timeFrame: Long

                if (difference < 86400000) {
                    timeFrame = 3600000
                    val hour = difference / timeFrame % 24
                    val timeLeft = "In about $hour hours"
                    countdown.text = timeLeft
                } else {
                    timeFrame = 86400000
                    val day = difference / timeFrame
                    val timeLeft = "In about $day days"
                    countdown.text = timeLeft
                }


            }
        }

        if (friend.review == 0) {
            binding.profileStars.rating = 0F
            binding.profileRating.text = "0.0"
            val noReview = "no review yet"
            binding.profileTotalreview.text = noReview
        } else {
            val rating = "%.${1}f".format(friend.rating / friend.review).toFloat()

            binding.profileRating.text = rating.toString()
            val total = "(${friend.review})"
            binding.profileTotalreview.text = total
            binding.profileStars.rating = rating
        }


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

    private fun popRemoveFriendDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.delete_friend_dialog, null)

        val text  = view.findViewById<TextView>(R.id.delete_friend_confirmation)
        text.text = "Are you sure you want to remove ${friend.nickname} from your friends?"

        builder.setView(view)
        val dialog = builder.create()

        val cancel: CardView = view.findViewById(R.id.cancel_card)
        val confirm: CardView = view.findViewById(R.id.remove_friend_card)

        dialog.show()

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        confirm.setOnClickListener {
            friendListViewModel.deleteFriend(sharedViewModel.currentUser.value!!, friend)
            dialog.dismiss()
            refresh()
        }


    }

    private fun refresh() {
        findNavController().popBackStack()
        val action =
            FriendListFragmentDirections.actionFriendListFragmentToFriendProfileFragment(friend)
        findNavController().navigate(action)
    }

    private fun showMoreActions() {
        val contentView: View

        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)

        if (isFriend!!) {
            contentView = View.inflate(context, R.layout.friend_moreactions, null)
            dialog.setContentView(contentView)

            val removeFriend: TextView = contentView.findViewById(R.id.removefriend_card)
            removeFriend.setOnClickListener {
                popRemoveFriendDialog()
                dialog.dismiss()
            }

        } else {
            contentView = View.inflate(context, R.layout.stranger_moreactions, null)
            dialog.setContentView(contentView)

            val message: TextView = contentView.findViewById(R.id.message_card)
            message.setOnClickListener {
                message()
                dialog.dismiss()
            }
        }


        dialog.show()

        val cancel: MaterialCardView = contentView.findViewById(R.id.cancel_card)
        val reportUser: TextView = contentView.findViewById(R.id.reportuser_card)
        val reviewUser: TextView = contentView.findViewById(R.id.reviewUser_card)


        reviewUser.setOnClickListener {
            goToReviewUser()
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        reportUser.setOnClickListener {
            goToReportUser()
            dialog.dismiss()
        }


    }

    private fun isFriend(username: String): Boolean {
        val friendUsernames: List<String> =
            friendListViewModel.friendList.value!!.map { it.username }
        return friendUsernames.contains(username)
    }

    private suspend fun exist(): Boolean {
        return friendListViewModel.doesRequestExist(
            sharedViewModel.getUser().username,
            friend.username
        )
    }

    private fun message() {
        if (friend.telegramHandle == "") {
            Toast.makeText(context, "The user has not set a Telegram handle", Toast.LENGTH_LONG)
                .show()
        } else {
            launchTelegram()
        }
    }

    private fun sendFriendRequest() {
        val request = Request(
            Request.FRIENDREQUEST,
            sharedViewModel.getUser().username,
            friend.username,
            Timestamp.now().toDate().time
        )
        friendListViewModel.sendFriendRequest(request)
    }

    private fun goToReportUser() {
        val action =
            FriendProfileFragmentDirections.actionFriendProfileFragmentToReportUserFragment(friend.username, friend.nickname)
        findNavController().navigate(action)
    }

    private fun goToReviewUser() {
        val action =
            FriendProfileFragmentDirections.actionFriendProfileFragmentToReviewUserFragment(friend)
        findNavController().navigate(action)
    }


}


