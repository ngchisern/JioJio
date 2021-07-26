package com.example.producity.ui.myactivity.myactivitydetail

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.MyActivityDetailBinding
import com.example.producity.models.Activity
import com.example.producity.models.Message
import com.example.producity.ui.myactivity.myactivitydetail.invite.MyActivityInviteFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import timber.log.Timber
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * A fragment representing a list of Items.
 */
class MyActivityDetailFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val myActivityDetailViewModel: MyActivityDetailViewModel by activityViewModels {
        MyActivityDetailViewModelFactory(
            ServiceLocator.provideParticipantRepository(),
            ServiceLocator.provideActivityRepository()
        )
    }

    private var isOwner: Boolean = false

    private var _binding: MyActivityDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())
        val temp: Activity = myActivityDetailViewModel.currentActivity ?: arguments.event

        isOwner = sharedViewModel.getUser().username == temp.owner

        _binding = MyActivityDetailBinding.inflate(inflater, container, false)

        if (isOwner) {
            binding.topAppBar.inflateMenu(R.menu.owner_activity_top_app_bar)
        } else {
            binding.topAppBar.inflateMenu(R.menu.activity_top_app_bar)
        }

        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())
        val activity: Activity =  myActivityDetailViewModel.currentActivity ?: arguments.event

        myActivityDetailViewModel.updateList(activity.docId)
        myActivityDetailViewModel.setActivity(activity)

        val recycleView = binding.participantRecyclerView

        var isOne = true

        val username = sharedViewModel.getUser().username

        val manager1 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val adapter1 = ParticipantlAdapter(this, false, false, username, myActivityDetailViewModel)

        val manager2 = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        val adapter2 = ParticipantlAdapter(this, true, false, username, myActivityDetailViewModel)

        recycleView.layoutManager = manager1
        recycleView.adapter = adapter1

        binding.participantShow.setOnClickListener {
            if (isOne) {
                it.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                isOne = false
                recycleView.layoutManager = manager2
                recycleView.adapter = adapter2
            } else {
                it.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                isOne = true
                recycleView.layoutManager = manager1
                recycleView.adapter = adapter1
            }
        }

        myActivityDetailViewModel.participantList.observe(viewLifecycleOwner) {
            adapter1.submitList(it)
            adapter2.submitList(it)
        }

        updateLayout(activity)
        trackListener(activity)
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
            myActivityDetailViewModel.loadActivityImage(activity.docId, activityImage)
            myActivityDetailViewModel.loadUserImage(activity.owner, activityCreatorIcon)
            myActivityDetailViewModel.loadNickname(activity.owner, activityDetailCreator)
            activityDetailTitle.text = activity.title
            activityDetailDate.text = dateFormat.format(activity.date)
            activityDetailTime.text = timeFormat.format(activity.date)
            activityDetailDescription.text = activity.description
            activityDetailCreator.text = activity.owner
            participantShow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            activityDetailParticipant.text = "${activity.participant.size}/${activity.pax}"

            if (activity.isVirtual) {
                activityDetailLocation.text = "Online"
            } else {
                val geocoder = Geocoder(context, Locale.getDefault())

                val address = geocoder.getFromLocation(activity.latitude, activity.longitude, 1)

                if (address.isEmpty()) {
                    activityDetailLocation.text = "Unknown"
                } else {
                    activityDetailLocation.text = address[0].getAddressLine(0)
                }

            }

        }
    }

    private fun trackListener(event: Activity) {
        val activity = myActivityDetailViewModel.currentActivity!!

        binding.inviteButton.setOnClickListener {
            MyActivityInviteFragment()
                .show(requireActivity().supportFragmentManager, "Bottom sheet Dialog Fragment")
        }

        binding.chatButton.setOnClickListener {
            val action =
                MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToMyActivityLogFragment(
                    event
                )
            findNavController().navigate(action)
        }

        binding.topAppBar.setNavigationOnClickListener {
            Timber.d("navigateup")
            myActivityDetailViewModel.currentActivity = null
            findNavController().navigateUp()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.leave -> {
                    popLeaveDialog()
                    true
                }
                R.id.manage -> {
                    val action =
                        MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToMyActivityManageFragment(
                            event
                        )
                    findNavController().navigate(action)
                    true
                }
                R.id.share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, "come and join ${activity.title} in JioJio")
                        putExtra(Intent.EXTRA_STREAM, URL("https://www.google.com/url?sa=i&url=https%3A%2F%2Fcomicbook.com%2Fanime%2Fnews%2Fjojos-bizarre-adventure-meets-gap-in-hilarious-anime-meme%2F&psig=AOvVaw1x-mC8KwAfeditG9n7KZ-k&ust=1627029968002000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCIiM1a-l9vECFQAAAAAdAAAAABAD")
                            )
                        type = "*/*";
                        //putExtra(Intent.EXTRA_STREAM, URL(activity.imageUrl).toURI())
                    }
                    startActivity(Intent.createChooser(intent, null))
                    true
                }
                R.id.report -> {
                    val intent = MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToReportUserFragment(activity.docId, activity.title)
                    findNavController().navigate(intent)
                    true
                }
                else -> true
            }
        }

    }

    private fun popLeaveDialog() {
        if (isOwner) {
            val builder = MaterialAlertDialogBuilder(requireContext())

            builder.setMessage("You are the creator of this activity. Leave the activity?")
                .setPositiveButton("Confirm") { _, _ ->
                    val user = sharedViewModel.getUser()
                    myActivityDetailViewModel.removeParticipant(user.username)
                    myActivityDetailViewModel.assignNewOwner()

                    val message = Message("left the activity.", user.username, Timestamp.now().toDate().time)
                    myActivityDetailViewModel.sendMessage(message)

                    val action = MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToNavigationMyActivity()
                    findNavController().navigate(action)
                    myActivityDetailViewModel.currentActivity = null
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            builder.show()
        } else {
            val builder = MaterialAlertDialogBuilder(requireContext())

            builder.setMessage("Leave the activity?")
                .setPositiveButton("Confirm") { _, _ ->

                    val username = sharedViewModel.currentUser.value!!.username
                    myActivityDetailViewModel.removeParticipant(username)

                    val message = Message("left the activity.", username, Timestamp.now().toDate().time)
                    myActivityDetailViewModel.sendMessage(message)

                    val action = MyActivityDetailFragmentDirections.actionScheduleDetailFragmentToNavigationMyActivity()
                    findNavController().navigate(action)
                    myActivityDetailViewModel.currentActivity = null
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }

            builder.show()
        }
    }

}