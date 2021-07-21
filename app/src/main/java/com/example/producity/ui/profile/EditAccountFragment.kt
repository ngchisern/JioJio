package com.example.producity.ui.profile

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.EditAccountBinding
import com.example.producity.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val CHANGE_EMAIL = 0
const val CHANGE_TELEGRAM = 1
const val CHANGE_PASSWORD = 2

class EditAccountFragment: Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory(ServiceLocator.provideUserRepository(), ServiceLocator.provideAuthRepository())
    }

    private var _binding: EditAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var userProfile: User // current user profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = EditAccountBinding.inflate(inflater, container, false)
        val root = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfile = sharedViewModel.currentUser.value!!

        updateLayout()
        listen()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateLayout() {
        val arguments = EditAccountFragmentArgs.fromBundle(requireArguments())

        if(arguments.isVerify) {
            loadVerifyLayout()
        } else {
            val code = EditAccountFragmentArgs.fromBundle(requireArguments()).code

            when (code) {
                CHANGE_EMAIL -> {
                    loadEmailLayout()
                }
                CHANGE_TELEGRAM -> {
                    loadTelegramLayout()
                }
                CHANGE_PASSWORD -> {
                    loadPasswordLayout()
                }
            }
        }


        binding.editAccountInput.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.editAccountInput, InputMethodManager.SHOW_IMPLICIT)

    }

    private fun loadEmailLayout() {
        val currentEmail = Firebase.auth.currentUser!!.email

        binding.editAccountTitle.text = "CHANGE EMAIL"
        binding.editAccountSubtitle.text = "Your current email address is $currentEmail.\n What would you like to change it to?"
        binding.editAccountSubject.text = "EMAIL"
        binding.editAccountInput.hint = "Email"
        binding.editAccountButton.text = "Change Email"

    }

    private fun loadTelegramLayout() {
        val tele = userProfile.telegramHandle

        if(tele == "") {
            binding.editAccountTitle.text = "ENTER YOUR TELEGRAM HANDLE"
            binding.editAccountSubtitle.text = "Please enter your own telegram handle without the @-prefix "
            binding.editAccountButton.text = "Next"
        } else {
            binding.editAccountTitle.text = "CHANGE TELEGRAM HANDLE"
            binding.editAccountButton.text = "Change Telegram Handle"
            binding.editAccountSubtitle.text = "Your current telegram handle is $tele.\n What would you like to change it to?"
        }

        binding.editAccountSubject.text = "TELEGRAM HANDLE"
        binding.editAccountInput.hint = "Telegram Handle"

    }

    private fun loadPasswordLayout() {
        binding.editAccountTitle.text = "CHANGE PASSWORD"
        binding.editAccountSubtitle.text = "Choose a password that's harder for people to guess"
        binding.editAccountSubject.text = "NEW PASSWORD"
        binding.editAccountInput.hint = ""
        binding.editAccountButton.text = "Change Password"
    }

    private fun loadVerifyLayout() {
        binding.editAccountTitle.text = "VERIFY YOUR PASSWORD"
        binding.editAccountSubtitle.text = "Please enter your password to edit your account"
        binding.editAccountSubject.text = "PASSWORD"
        binding.editAccountInput.hint = ""
        binding.editAccountButton.text = "Done"
        binding.closeText.text = "Back"
        binding.editAccountInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    }


    private fun listen() {
        binding.closeText.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.editAccountButton.setOnClickListener {
            val arguments =  EditAccountFragmentArgs.fromBundle(requireArguments())

            if(arguments.isVerify) {
                performVerification()
                return@setOnClickListener
            }

            val code = arguments.code
            when(code) {
                CHANGE_EMAIL -> {
                    performChangeEmail()
                }
                CHANGE_TELEGRAM -> {
                    performChangeTele()
                }
                CHANGE_PASSWORD -> {
                    performChangePassword()
                }
            }
        }
    }

    private fun performChangeEmail() {
        val email = binding.editAccountInput.text.toString()
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editAccountInput.error = "Not a well formed email address"
            return
        }
        val action = EditAccountFragmentDirections.actionEditAccountFragmentSelf(CHANGE_EMAIL, email, true)
        findNavController().navigate(action)
    }

    private fun performChangeTele() {
        val tele = binding.editAccountInput.text.toString()

        val action = EditAccountFragmentDirections.actionEditAccountFragmentSelf(CHANGE_TELEGRAM, tele, true)
        findNavController().navigate(action)
    }

    private fun performChangePassword() {
        val password = binding.editAccountInput.text.toString()

        if(password.length < 6) {
            binding.editAccountInput.error = "Password must contain at least 6 characters."
            return
        }
        val action = EditAccountFragmentDirections.actionEditAccountFragmentSelf(CHANGE_PASSWORD, password, true)
        findNavController().navigate(action)
    }

    private fun performVerification() {
        val password = binding.editAccountInput.text.toString()
        if(!profileViewModel.verifyPassword(password)) {
            binding.editAccountInput.error = "Password does not match"
            return
        }

        val code = EditAccountFragmentArgs.fromBundle(requireArguments()).code
        val input = EditAccountFragmentArgs.fromBundle(requireArguments()).input

        when(code) {
            CHANGE_EMAIL -> {
                profileViewModel.changeEmail(input)
                findNavController().popBackStack(R.id.navigation_profile, false)
            }
            CHANGE_TELEGRAM -> {
                val editedUserProfile = userProfile
                editedUserProfile.telegramHandle = input
                profileViewModel.updateUserProfile(editedUserProfile)
                sharedViewModel.updateUser(editedUserProfile)
                findNavController().popBackStack(R.id.navigation_profile, false)
            }
            CHANGE_PASSWORD -> {
                profileViewModel.changePassword(input)
                findNavController().popBackStack(R.id.navigation_profile, false)
            }
        }
    }

}