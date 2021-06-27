package com.example.producity.ui.explore.exploredetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.ExploreDetailBinding
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.example.producity.ui.explore.ExploreViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ExploreDetailFragment: Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val exploreViewModel: ExploreViewModel by activityViewModels()

    private var _binding: ExploreDetailBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore


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
        val activity: Activity = exploreViewModel.friendActivities.value?.get(arguments.position)?: return

        updateLayout(activity)
        trackListener(arguments.position)
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
            Picasso.get().load(activity.imageUrl).into(exploreDetailImage)
            Picasso.get().load(activity.ownerImageUrl).into(creatorImage)
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

    private fun trackListener(position: Int) {

        binding.joinButton.setOnClickListener {
            addToFirestore(position)
        }
        binding.cancelButton.setOnClickListener {
            val action = ExploreDetailFragmentDirections.actionExploreDetailFragmentToNavigationExplore()
            findNavController().navigate(action)
        }
    }

    private fun addToFirestore(position: Int) {

        val username = sharedViewModel.currentUser.value?.username ?: return
        Toast.makeText(context, "adding to firestore", Toast.LENGTH_SHORT).show()

        val docId = exploreViewModel.friendActivities.value!![position].docId

        val union = hashMapOf<String, Any>(
            "participant" to FieldValue.arrayUnion(username)
        )

        db.document("activity/$docId")
            .update(union)
            .addOnSuccessListener {
                Log.d("Main", "DocumentSnapshot successfully updated!")
                addToDatabase(position)
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }
    }

    private fun addToDatabase(position: Int) {
        val user = sharedViewModel.currentUser.value?: return
        Toast.makeText(context, "adding to database", Toast.LENGTH_SHORT).show()

        val rtdb = Firebase.database

        val docId = exploreViewModel.friendActivities.value!![position].docId

        val participant = Participant(user.imageUrl,
            user.displayName,
            user.username,
            docId)

        rtdb.getReference().child("participant/$docId").push()
            .setValue(participant)
            .addOnSuccessListener {
                Log.d("Main", "added participant")
            }
            .addOnFailureListener {
                Log.d("Main", it.message.toString())
            }
    }

}