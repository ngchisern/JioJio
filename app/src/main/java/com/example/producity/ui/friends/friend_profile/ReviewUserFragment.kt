package com.example.producity.ui.friends.friend_profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.ReviewUserBinding
import com.example.producity.models.Review
import com.example.producity.models.User
import com.example.producity.ui.friends.my_friends.FriendListFragmentDirections
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch


class ReviewUserFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val reviewViewModel: ReviewUserViewModel by viewModels {
        ReviewViewModelFactory(ServiceLocator.provideUserRepository())
    }

    private var _binding: ReviewUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var userProfile: User // current user profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ReviewUserBinding.inflate(inflater, container, false)
        val root = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfile = ReviewUserFragmentArgs.fromBundle(requireArguments()).user

        updateLayout()
        listen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateLayout() {

        viewLifecycleOwner.lifecycleScope.launch {
            val exist =
                reviewViewModel.exist(sharedViewModel.getUser().username, userProfile.username)

            val subtitle =
                "Leave an honest review and let other users know what you think about ${userProfile.nickname}."
            binding.reviewSubtitle.text = subtitle

            val title = "Review ${userProfile.nickname}"
            binding.reviewTitle.text = title

            if (exist != null) {
                binding.reviewStar.rating = exist.rating
                binding.reviewDescription.setText(exist.description)
                val update = "Update"
                binding.submitText.text = update

            }
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun listen() {
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.reviewStar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val touchPositionX = event.x
                val width: Int = binding.reviewStar.width
                val starsf = touchPositionX / width * 5.0f
                val stars = starsf.toInt() + 1

                binding.reviewStar.rating = stars.toFloat()
                v.isPressed = false
            }
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.isPressed = true
            }
            if (event.action == MotionEvent.ACTION_CANCEL) {
                v.isPressed = false
            }
            true
        }

        binding.submitText.setOnClickListener {
            val reviewer = sharedViewModel.getUser().username
            val reviewee = userProfile.username
            val rating = binding.reviewStar.rating
            val description = binding.reviewDescription.text.toString()

            val last = reviewViewModel.exist

            val editedUserProfile: User

            if (last == null) {
                editedUserProfile = User(
                    userProfile.username,
                    userProfile.uid,
                    userProfile.nickname,
                    userProfile.telegramHandle,
                    userProfile.gender,
                    userProfile.birthday,
                    userProfile.bio,
                    userProfile.banner,
                    userProfile.rating + rating,
                    userProfile.review + 1,
                    userProfile.latitude,
                    userProfile.longitude
                )
                userProfile.rating += rating
                userProfile.review++

            } else {
                editedUserProfile = User(
                    userProfile.username,
                    userProfile.uid,
                    userProfile.nickname,
                    userProfile.telegramHandle,
                    userProfile.gender,
                    userProfile.birthday,
                    userProfile.bio,
                    userProfile.banner,
                    userProfile.rating + (rating - last.rating),
                    userProfile.review,
                    userProfile.latitude,
                    userProfile.longitude
                )
                userProfile.rating = userProfile.rating + (rating - last.rating)
            }


            reviewViewModel.submitReview(
                Review(
                    reviewer,
                    reviewee,
                    rating,
                    description,
                    Timestamp.now().toDate().time
                ), editedUserProfile
            )
            val id = R.id.friendProfileFragment
            findNavController().popBackStack(id, true)
            val action =
                FriendListFragmentDirections.actionFriendListFragmentToFriendProfileFragment(
                    editedUserProfile
                )
            findNavController().navigate(action)
        }

    }


}