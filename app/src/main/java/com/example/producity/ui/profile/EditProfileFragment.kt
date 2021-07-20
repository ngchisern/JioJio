package com.example.producity.ui.profile

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentEditProfileBinding
import com.example.producity.models.User
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.RatingType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val SELECT_PROFILE_PIC_REQUEST = 1

class EditProfileFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory(
            ServiceLocator.provideUserRepository(),
            ServiceLocator.provideAuthRepository()
        )
    }

    private lateinit var userProfile: User // current user profile

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var profilePicUri: Uri? = null // used to upload new profile picture to database
    private var bannerUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

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
        listen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_PROFILE_PIC_REQUEST && resultCode == RESULT_OK && data != null) {

            val profilePic = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, profilePic)

            val profilePicImageView: ImageView = binding.editProfilePic
            profilePicImageView.setImageBitmap(bitmap)

            profilePicUri = profilePic
        }



        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun listen() {
        binding.datePicker.setOnClickListener {
            showDatePickerDialog(requireView())
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

        // back button
        val toolbar = binding.topAppBar
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

        binding.editProfileBanner.setOnClickListener {
            showGif()
        }


    }

    private fun loadProfile() {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        Picasso.get().load(sharedViewModel.userImage.value!!).into(binding.editProfilePic)

        binding.editDisplayName.setText(userProfile.nickname)
        binding.birthday.text = dateFormat.format(userProfile.birthday)
        binding.editBio.setText(userProfile.bio)

        if (userProfile.latitude == -1.0 && userProfile.longitude == -1.0) {
            binding.editLocation.setText("Not selected yet")
        } else {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(userProfile.latitude, userProfile.longitude, 1)

            binding.editLocation.setText("${address[0].locality}, ${address[0].countryName}")
        }


        Glide.with(requireContext()).load(userProfile.banner).into(binding.editProfileBanner)
    }

    private fun uploadImageToFirebaseStorageAndEditProfile() {
        val profilePicUri = profilePicUri

        val uid = sharedViewModel.currentUser.value!!.uid
        val displayName = binding.editDisplayName.text.toString()
        val bio = binding.editBio.text.toString()
        val banner = if (bannerUrl == null) userProfile.banner else bannerUrl!!

        val birthdate = if (datePickerFragment == null || datePickerFragment!!.birthdate == null)
            sharedViewModel.currentUser.value!!.birthday else datePickerFragment!!.birthdate

        val rating = sharedViewModel.currentUser.value!!.rating
        val review = sharedViewModel.currentUser.value!!.review

        val gender = when (binding.genderOptions.checkedRadioButtonId) {
            R.id.male_button -> 0
            R.id.female_button -> 1
            R.id.other_button -> 2
            else -> -1
        }

        GlobalScope.launch {

            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocationName(binding.editLocation.text.toString(), 1)
            val lat = if (address.size == 0) -1.0 else address[0].latitude
            val long = if (address.size == 0) -1.0 else address[0].longitude

            val editedUserProfile = User(
                userProfile.username,
                uid,
                displayName,
                userProfile.telegramHandle,
                gender,
                birthdate!!,
                bio,
                banner,
                rating,
                review,
                lat,
                long
            )

            if (profilePicUri == null) { // no new image selected

                CoroutineScope(Dispatchers.Main).launch {
                    profileViewModel.updateUserProfile(editedUserProfile)
                    // with current imageUrl, no need to upload image again
                    sharedViewModel.updateUser(editedUserProfile)
                    showEditSuccessfulDialog()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val returnedUser = profileViewModel.uploadImageToFirebaseStorageAndEditProfile(
                        profilePicUri, editedUserProfile
                    )

                    sharedViewModel.currentUser.postValue(returnedUser)
                    sharedViewModel.userImage.postValue(profilePicUri.toString())
                    sharedViewModel.loadUserImage()
                    ContextCompat.getMainExecutor(context).execute {
                        showEditSuccessfulDialog()
                    }
                }
            }

            if (userProfile.nickname != editedUserProfile.nickname) {
                profileViewModel.updateDatabase(userProfile.username, editedUserProfile.nickname)
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
        findNavController().navigateUp()
    }

    class DatePickerFragment(private val prevView: View) :
        DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        var birthdate: Date? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            return DatePickerDialog(this.requireContext(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            // set the text beside the datePickerButton to show the date
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            birthdate = Date(year - 1900, month, day)
            val birthdayText = prevView.findViewById<TextView>(R.id.birthday)
            birthdayText.text = dateFormat.format(birthdate!!)
        }
    }

    private var datePickerFragment: DatePickerFragment? = null

    private fun showDatePickerDialog(view: View) {
        datePickerFragment = DatePickerFragment(view)
        datePickerFragment!!.show(requireFragmentManager(), "datePicker")
    }


    private fun showGif() {
        Giphy.configure(requireContext(), "SI2smNr9Iy26zs2LkuDYFbDrUbsS1qo8")

        val settings = GPHSettings(GridType.waterfall, GPHTheme.Light)

        settings.rating = RatingType.r
        val gifsDialog = GiphyDialogFragment.newInstance(settings)
        settings.showSuggestionsBar = false

        gifsDialog.show(requireActivity().supportFragmentManager, "giphy_dialog")

        gifsDialog.gifSelectionListener = object : GiphyDialogFragment.GifSelectionListener {

            override fun didSearchTerm(term: String) {

            }

            override fun onDismissed(selectedContentType: GPHContentType) {
            }

            override fun onGifSelected(
                media: Media,
                searchTerm: String?,
                selectedContentType: GPHContentType
            ) {
                val url = media.images.original!!.gifUrl

                Glide.with(requireContext()).load(url).into(binding.editProfileBanner)
                bannerUrl = url.toString()

            }

        }
    }


}