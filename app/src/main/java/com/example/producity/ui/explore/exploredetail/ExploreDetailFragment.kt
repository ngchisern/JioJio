package com.example.producity.ui.explore.exploredetail

import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.ExploreDetailBinding
import com.example.producity.models.Activity
import com.example.producity.models.Message
import com.example.producity.ui.explore.ExploreViewModel
import com.example.producity.ui.explore.ExploreViewModelFactory
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ExploreDetailFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val exploreViewModel: ExploreViewModel by activityViewModels() {
        ExploreViewModelFactory(
            ServiceLocator.provideParticipantRepository(),
            ServiceLocator.provideActivityRepository()
        )
    }

    private var _binding: ExploreDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExploreDetailBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = ExploreDetailFragmentArgs.fromBundle(requireArguments())
        val activity: Activity = arguments.event

        updateLayout(activity)
        trackListener(activity)
    }

    override fun onDestroyView() {
        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true
        super.onDestroyView()
        _binding = null
    }

    private fun updateLayout(activity: Activity) {
        if (_binding == null) return

        val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

        _binding?.apply {
            exploreViewModel.loadActivityImage(activity.docId, exploreDetailImage)
            exploreViewModel.loadUserImage(activity.owner, creatorImage)
            exploreDetailTitle.text = activity.title
            exploreDetailDate.text = dateFormat.format(activity.date)
            exploreDetailTime.text = timeFormat.format(activity.date)
            exploreDetailDescription.text = activity.description
            exploreViewModel.loadNickname(activity.owner, exploreDetailCreator)

            val text = "+${activity.participant.size}/${activity.pax} going"
            exploreDetailParticipant.text = text

            if (activity.isVirtual) {
                val onlinetext = "Online"
                exploreDetailLocation.text = onlinetext
            } else {
                val geocoder = Geocoder(context, Locale.getDefault())

                val address = geocoder.getFromLocation(activity.latitude, activity.longitude, 1)

                if (address.isEmpty()) {
                    val unknown = "Unknown"
                    exploreDetailLocation.text = unknown
                } else {
                    exploreDetailLocation.text = address[0].getAddressLine(0)
                }

                if(activity.participant.contains(sharedViewModel.getUser().username)) {
                    val joined = "Joined"
                    binding.joinButton.text = joined
                    binding.joinButton.setBackgroundColor(Color.parseColor("#00ffd2"))
                    binding.joinButton.isClickable = false
                } else if (activity.participant.size >= activity.pax) {
                    val full = "Full"
                    binding.joinButton.text = full
                    binding.joinButton.isClickable = false
                }

            }
        }

    }

    private fun trackListener(event: Activity) {
        binding.joinButton.setOnClickListener {
            joinActivity(event)

            val activity: Activity = ExploreDetailFragmentArgs.fromBundle(requireArguments()).event
            val participants = activity.participant.toMutableList()
            participants.add(sharedViewModel.getUser().username)
            activity.participant = participants

            val joined = "Joined"
            binding.joinButton.text = joined
            binding.joinButton.setBackgroundColor(Color.parseColor("#00ffd2"))
            binding.joinButton.isClickable = false
        }


        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun joinActivity(event: Activity) {
        val docId = event.docId
        val user = sharedViewModel.getUser()

        val message = Message("Hello, everyone. I just joined this activity.", user.username, Timestamp.now().toDate().time )
        exploreViewModel.sendMessage(message, docId)

        exploreViewModel.addParticipant(sharedViewModel.getUser(), docId)
        exploreViewModel.addRecommendation(event.label!!, sharedViewModel.getUser().username)
    }

}