package com.example.producity.ui.myactivity.myactivitydetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.databinding.MyActivityDetailBinding
import com.example.producity.models.Activity
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.example.producity.ui.myactivity.myactivitydetail.invite.MyActivityInviteFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * A fragment representing a list of Items.
 */
class MyActivityDetailFragment : Fragment() {

    private val myActivityViewModel: MyActivityViewModel by activityViewModels()
    private val friendViewModel: FriendListViewModel by activityViewModels()

    private var isOwner: Boolean = false

    private var _binding: MyActivityDetailBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())
        val temp: Activity? = myActivityViewModel.myActivityList.value?.get(arguments.position)

        isOwner = friendViewModel.currentUser.value?.username.equals(temp?.owner)


        _binding = MyActivityDetailBinding.inflate(inflater, container, false)

        if (isOwner) {
            binding.topAppBar.inflateMenu(R.menu.owner_activity_top_app_bar)
        } else {
            binding.topAppBar.inflateMenu(R.menu.activity_top_app_bar)
        }


        val root: View = binding.root


        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        val floatingButton: View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = false

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())

        val activity: Activity =
            myActivityViewModel.listInCharge.get(arguments.position) 

        updateLayout(activity)
        trackListener(arguments.position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateLayout(activity: Activity) {
        if (_binding == null) return

        val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

        _binding?.apply {
            Picasso.get().load(activity.imageUrl).into(activityImage)
            Picasso.get().load(activity.ownerImageUrl).into(activityCreatorIcon)
            activityDetailTitle.text = activity.title
            activityDetailDate.text = dateFormat.format(activity.date)
            activityDetailTime.text = timeFormat.format(activity.date)
            activityDetailDescription.text = activity.description
            activityDetailCreator.text = activity.owner


            if(!activity.isVirtual) {
                activityDetailLocation.text = activity.location
            } else {
                val text = "Online"
                activityDetailLocation.text = text
            }

        }
    }

    private fun trackListener(position: Int) {
        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())

        val activity: Activity = myActivityViewModel.listInCharge.get(arguments.position)

        binding.inviteButton.setOnClickListener {
            Toast.makeText(context, "INVITE", Toast.LENGTH_SHORT).show()
            MyActivityInviteFragment(myActivityViewModel.documentIdInCharge[position])
                .show(requireActivity().supportFragmentManager, "Bottom sheet Dialog Fragment")
        }

        binding.logButton.setOnClickListener {
            Toast.makeText(context, "WHITE BOARD", Toast.LENGTH_SHORT).show()
            val action = MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToMyActivityLogFragment(position)
            findNavController().navigate(action)
        }

        binding.topAppBar.setNavigationOnClickListener {
            val action = MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToNavigationMyActivity()
            findNavController().navigate(action)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.leave -> {
                    val username = friendViewModel.currentUser.value?.username
                    val delete = hashMapOf<String, Any>(
                        "participant" to FieldValue.arrayRemove(username)
                    )

                    db.document("activity/${myActivityViewModel.documentIdInCharge[position]}")
                        .update(delete)
                        .addOnSuccessListener {
                            Log.d("Main", "Left")
                        }
                        .addOnFailureListener {
                            Log.d("Main", it.message.toString())
                        }
                    true
                }
                R.id.manage -> {
                    val action = MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToMyActivityManageFragment(position)
                    findNavController().navigate(action)
                    true
                }
                R.id.share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        data = Uri.parse("smsto:")  // This ensures only SMS apps respond
                        putExtra("sms_body", "come and join ${activity.title}")
                        putExtra(Intent.EXTRA_STREAM, URL(activity.imageUrl).toURI())
                    }
                    startActivity(Intent.createChooser(intent, null))
                    true
                }
                else -> true
            }
        }


    }


}