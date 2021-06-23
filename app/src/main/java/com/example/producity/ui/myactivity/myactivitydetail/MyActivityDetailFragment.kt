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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.MyActivityDetailBinding
import com.example.producity.models.Activity
import com.example.producity.models.Participant
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.example.producity.ui.myactivity.myactivitydetail.invite.MyActivityInviteFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
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

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val myActivityViewModel: MyActivityViewModel by activityViewModels()
    private val myActivityDetailViewModel: MyActivityDetailViewModel by activityViewModels()

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

        isOwner = sharedViewModel.currentUser.value?.username.equals(temp?.owner)


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

        val position = MyActivityDetailFragmentArgs.fromBundle(requireArguments()).position
        val activity: Activity

        if(myActivityViewModel.isUpcoming) {
            activity = myActivityViewModel.myActivityList.value!!.get(position)
            myActivityDetailViewModel.updateList(activity.docId)
        } else {
            activity = myActivityViewModel.pastActivityList.value!!.get(position)
            myActivityDetailViewModel.updateList(activity.docId)
        }

        val recycleView = binding.participantRecyclerView
        //recycleView.layoutManager = object : GridLayoutManager(context, 3) {
         //   override fun canScrollVertically(): Boolean {
         //       return false
          //  }
        //}

        var isOne = true

        val manager1 = LinearLayoutManager(context, RecyclerView.HORIZONTAL,  false)
        val adapter1 = ParticipantlAdapter(this, false, false)

        val manager2 = LinearLayoutManager(context, RecyclerView.VERTICAL,  true)
        val adapter2 = ParticipantlAdapter(this, true, false)

        recycleView.layoutManager = manager1
        recycleView.adapter = adapter1

        binding.participantShow.setOnClickListener {
            if(isOne) {
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
        trackListener(position)
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
            participantShow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            activityDetailParticipant.setText("${activity.participant.size}/${activity.pax}")

            if(!activity.isVirtual) {
                activityDetailLocation.text = activity.location
            } else {
                val text = "Online"
                activityDetailLocation.text = text
            }

        }
    }

    private fun trackListener(position: Int) {
        val activity: Activity

        if(myActivityViewModel.isUpcoming) {
            activity = myActivityViewModel.myActivityList.value!!.get(position)
        } else {
            activity = myActivityViewModel.pastActivityList.value!!.get(position)
        }

        binding.inviteButton.setOnClickListener {
            Toast.makeText(context, "INVITE", Toast.LENGTH_SHORT).show()
            MyActivityInviteFragment(activity.docId)
                .show(requireActivity().supportFragmentManager, "Bottom sheet Dialog Fragment")
        }

        binding.logButton.setOnClickListener {
            Toast.makeText(context, "activity log", Toast.LENGTH_SHORT).show()
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

                    val username = sharedViewModel.currentUser.value?.username ?: return@setOnMenuItemClickListener true
                    val delete = hashMapOf<String, Any>(
                        "participant" to FieldValue.arrayRemove(username)
                    )

                    db.document("activity/${activity.docId}")
                        .update(delete)
                        .addOnSuccessListener {
                            removeFromDatabase(activity.docId, username)
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

    private fun removeFromDatabase(docId: String, username: String) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/$docId")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(doc in snapshot.children) {
                        if(doc.getValue(Participant::class.java)?.username.equals(username)) {
                            doc.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to read value")
                }

            })

    }


}