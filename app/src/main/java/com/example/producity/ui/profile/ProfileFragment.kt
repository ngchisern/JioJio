package com.example.producity.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.LoginActivity
import com.example.producity.R
import com.example.producity.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_profile -> {
                    profileViewModel.selectedGender = profileViewModel.currentUserProfile.value!!.gender
                    // reset gender to prevent incorrect results
                    // (if another gender radio button is clicked but not saved)

                    navigateEditProfile()
                    true
                }
                R.id.sign_out -> {
                    quit()
                    true
                }
                else -> false
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()
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
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun loadProfile() {
        val userProfile = profileViewModel.currentUserProfile.value!!

        binding.displayName.text = userProfile.displayName
        binding.username.text = userProfile.username
        binding.telegramHandle.text = userProfile.telegramHandle
        binding.gender.text = userProfile.gender
        binding.birthday.text = userProfile.birthday
        binding.bio.text = userProfile.bio

        val imageView = binding.profilePic
        val imageUrl = userProfile.imageUrl
        Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(imageView)
    }

}