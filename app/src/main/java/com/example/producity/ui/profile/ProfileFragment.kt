package com.example.producity.ui.profile

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.producity.LoginActivity
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()
        listen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateEditProfile() {
        val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment()
        findNavController().navigate(action)
    }

    private fun quit() {
        Firebase.auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun loadProfile() {
        val userProfile = sharedViewModel.currentUser.value!!
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        Picasso.get().load(sharedViewModel.userImage.value!!).into(binding.profileImage)

        binding.profileNickname.text = userProfile.nickname
        binding.profileUsername.text = userProfile.username
        binding.profileTelehandle.text = userProfile.telegramHandle
        binding.profileBirthday.text = dateFormat.format(userProfile.birthday)
        binding.profileBio.text = userProfile.bio

        if(userProfile.latitude == -1.0 && userProfile.longitude == -1.0) {
            binding.profileLocation.text = "Not selected yet"
        } else {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(userProfile.latitude, userProfile.longitude, 1)

            binding.profileLocation.text = "${address[0].locality}, ${address[0].countryName}"
        }

        binding.profileGender.text = when(userProfile.gender) {
            0 -> "Male"
            1 -> "Female"
            2 -> "Other"
            else -> "Not yet selected"
        }



        val auth = Firebase.auth

        binding.profileEmail.text = auth.currentUser!!.email

        Glide.with(requireContext()).load(userProfile.banner).into(binding.profileBanner)

        if(userProfile.review == 0) {
            binding.profileStars.rating = 0F
            binding.profileRating.text = "0.0"
            binding.profileTotalreview.text = "no review yet"
        } else {
            val rating = "%.${1}f".format(userProfile.rating/userProfile.review).toFloat()

            binding.profileStars.rating = rating
            binding.profileRating.text = rating.toString()
            binding.profileTotalreview.text = "(${userProfile.review})"
        }


        //val imageView = binding.profilePic

        //Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(imageView)
    }

    private fun listen() {
        binding.logoutLayout.setOnClickListener {
            quit()
        }

        binding.editProfileCard.setOnClickListener {
            navigateEditProfile()
        }

        binding.viewFriendLayout.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToNavigationFriends()
            findNavController().navigate(action)
        }

        binding.emailLayout.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditAccountFragment(CHANGE_EMAIL)
            findNavController().navigate(action)
        }

        binding.telehandleLayout.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditAccountFragment(CHANGE_TELEGRAM)
            findNavController().navigate(action)
        }

        binding.passwordLayout.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditAccountFragment(CHANGE_PASSWORD)
            findNavController().navigate(action)
        }

        binding.viewMemoriesLayout.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToMemoryFragment()
            findNavController().navigate(action)
        }

        binding.ratingLayout.setOnClickListener {
            val userProfile = sharedViewModel.currentUser.value!!
            val action = ProfileFragmentDirections.actionNavigationProfileToReviewListFragment(userProfile)
            findNavController().navigate(action)
        }


    }

}