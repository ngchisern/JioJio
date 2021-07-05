package com.example.producity.ui.profile

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentEditProfileBinding
import com.example.producity.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.coroutines.*
import java.time.LocalDate
import java.util.*

private const val SELECT_PROFILE_PIC_REQUEST = 1

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory(ServiceLocator.provideUserRepository())
    }

    private lateinit var userProfile: User // current user profile

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var profilePicUri: Uri? = null // used to upload new profile picture to database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root = binding.root

        // back button
        val toolbar = binding.topAppBar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save_button -> {
                    uploadImageToFirebaseStorageAndEditProfile()
                    true
                }
                else -> false
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            sViewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        userProfile = sharedViewModel.currentUser.value!!

        loadProfile()

        binding.datePicker.setOnClickListener {
            showDatePickerDialog(view)
        }

        binding.editProfilePic.setOnClickListener {
            profilePicUri = null
            // set to null to avoid uploading duplicates to database when editing multiple times
            // even without changing profile picture

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                intent,
                SELECT_PROFILE_PIC_REQUEST
            ) // handle result in onActivityResult()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PROFILE_PIC_REQUEST && resultCode == RESULT_OK && data != null) {
            Log.d("EditProfileFragment", "Selected profile picture")

            val profilePic = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, profilePic)

            val profilePicImageView: ImageView = binding.editProfilePic
            profilePicImageView.setImageBitmap(bitmap)

            profilePicUri = profilePic
        }
    }

    private fun loadProfile() {
        binding.editDisplayName.setText(userProfile.displayName)
        binding.username.text = userProfile.username
        binding.editTelegramHandle.setText(userProfile.telegramHandle)
        binding.birthday.text = userProfile.birthday
        binding.editBio.setText(userProfile.bio)

        val imageView = binding.editProfilePic
        val imageUrl = userProfile.imageUrl
        Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(imageView)
    }

    private fun uploadImageToFirebaseStorageAndEditProfile() {
        var profilePicUri = profilePicUri

        val uid = sharedViewModel.currentUser.value!!.uid
        val displayName = binding.editDisplayName.text.toString()
        val username = binding.username.text.toString()
        val telegramHandle = binding.editTelegramHandle.text.toString()
        val gender = if (binding.maleButton.isChecked) "Male" else "Female"
        val birthday = binding.birthday.text.toString()
        val bio = binding.editBio.text.toString()

        val editedUserProfile = User(
            username, uid, displayName, telegramHandle, gender, birthday, bio, userProfile.imageUrl
        )

        if (profilePicUri == null) { // no new image selected
            profileViewModel.updateUserProfile(editedUserProfile)
            // with current imageUrl, no need to upload image again
            sharedViewModel.updateUser(editedUserProfile)
            showEditSuccessfulDialog()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val returnedUser = profileViewModel.uploadImageToFirebaseStorageAndEditProfile(
                    profilePicUri, editedUserProfile
                )
                Log.d(
                    "EditProfileFragment",
                    "returnedUser: $returnedUser username: ${returnedUser.username}"
                )
                sharedViewModel.currentUser.postValue(returnedUser)
                ContextCompat.getMainExecutor(context).execute {
                    showEditSuccessfulDialog()
                }
            }
        }
    }

    private fun showEditSuccessfulDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Profile updated!")
            .setPositiveButton("Done") { dialog, _ ->
                dialog.cancel()
                navigateToProfile()
            }
            .show()
    }

    private fun navigateToProfile() {
        val action = EditProfileFragmentDirections.actionEditProfileFragmentToNavigationProfile()
        findNavController().navigate(action)
    }


    class DatePickerFragment(private val prevView: View) :
        DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            return DatePickerDialog(this.requireContext(), this, year, month, day)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            // set the text beside the datePickerButton to show the date
            val date = LocalDate.of(year, month + 1, day)
            val birthdayText = prevView.findViewById<TextView>(R.id.birthday)
            birthdayText.text = date.toString()
        }
    }

    private fun showDatePickerDialog(view: View) {
        val datePickerFragment = DatePickerFragment(view)
        datePickerFragment.show(requireFragmentManager(), "datePicker")
    }
}