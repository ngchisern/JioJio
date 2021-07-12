package com.example.producity.ui.explore.exploredetail

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
import com.example.producity.models.Participant
import com.example.producity.ui.explore.ExploreViewModel
import com.example.producity.ui.explore.ExploreViewModelFactory
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ExploreDetailFragment: Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val exploreViewModel: ExploreViewModel by activityViewModels() {
        ExploreViewModelFactory(ServiceLocator.provideParticipantRepository(), ServiceLocator.provideActivityRepository())
    }

    private var _binding: ExploreDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ExploreDetailBinding.inflate(inflater,container,false)

        val root: View  = binding.root

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
        if(_binding == null) return

        val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

        _binding?.apply {
            //Picasso.get().load(activity.imageUrl).into(exploreDetailImage)
            //Picasso.get().load(activity.ownerImageUrl).into(creatorImage)
            exploreDetailTitle.text = activity.title
            exploreDetailDate.text = dateFormat.format(activity.date)
            exploreDetailTime.text = timeFormat.format(activity.date)
            exploreDetailDescription.text = activity.description
            exploreDetailCreator.text = activity.owner

            if(!activity.isVirtual) {
                exploreDetailLocation.text = activity.location
            } else {
                exploreDetailLocation.setText("Online")
            }
        }

    }

    private fun trackListener(event: Activity) {

        binding.joinButton.setOnClickListener {
            addToFirestore(event)
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun addToFirestore(event: Activity) {
        val user = sharedViewModel.currentUser.value ?: return

        val docId = event.docId

        val participant = Participant(user.nickname,
            user.username)

        exploreViewModel.addParticipant(participant, docId)
    }

}