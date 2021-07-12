package com.example.producity.ui.myactivity

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentHomeBinding
import com.example.producity.models.Activity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MyActivityFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val myActivityViewModel: MyActivityViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    private var timer = Timer()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val root: View = binding.root

        auth = Firebase.auth
        db = Firebase.firestore

        val upcomingAdapter = MyActivityAdapter(this, myActivityViewModel)
        val recyclerView = binding.scheduleRecycleView

        recyclerView.adapter = upcomingAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        myActivityViewModel.myActivityList.observe(viewLifecycleOwner) {
            if(it.isEmpty()) return@observe

            upcomingAdapter.submitList(it.subList(1, it.size))
        }


        val viewPager = binding.homePager
        val pagerAdapter = PagerAdapter(this)
        viewPager.adapter = pagerAdapter

        val tabLayout = binding.tabLayout

        myActivityViewModel.myActivityList.observe(viewLifecycleOwner) {
            pagerAdapter.submitList(it)
        }

        var count = 0
        timer = Timer()
        timer.schedule(object: TimerTask(){

            override fun run() {
                // handle empty count
                if(pagerAdapter.itemCount == 0) return

                requireActivity().runOnUiThread {
                    viewPager.setCurrentItem(count%pagerAdapter.itemCount)
                }

                count ++
            }

        },5000, 5000)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

        }.attach()




        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            hViewModel = myActivityViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        updateLayout()
        listen()

    }

    override fun onDestroyView() {
        timer.cancel()
        super.onDestroyView()
    }

    private fun updateLayout() {

        sharedViewModel.userImage.observe(viewLifecycleOwner) { url ->
            if(url == "") return@observe

            Picasso.get().load(url).into(binding.profileImage)

        }

        myActivityViewModel.myActivityList.observe(viewLifecycleOwner) {
            if(it.isEmpty()) return@observe

            val subject = it[0]
            updatePrimaryActivity(subject)

        }

    }

    private fun listen() {
        binding.homePrimaryEvent.setOnClickListener {
            val action = MyActivityFragmentDirections.actionNavigationHomeToScheduleDetailFragment(myActivityViewModel.getActivity(0))
            findNavController().navigate(action)
        }

        binding.welcomeNoti.setOnClickListener {
            val action = MyActivityFragmentDirections.actionNavigationMyActivityToNotificationFragment()
            findNavController().navigate(action)
        }
    }

    private fun updatePrimaryActivity(subject: Activity) {
        binding.apply {
            //primaryCountdownText.setText()
            primaryTitle.text = subject.title
            primaryDescription.text = subject.description
            primaryLocation.text = subject.location

            val format = SimpleDateFormat("d MMM yyyy hh:mm aaa", Locale.getDefault())
            primaryTime.text = format.format(subject.date)

            val difference = subject.date.time - Timestamp.now().toDate().time
            val timeFrame: Long

            if(difference < 86400000) {
                timeFrame = 3600000
            } else {
                timeFrame = 86400000
            }

            val countDownTimer = object: CountDownTimer(difference, timeFrame) {
                override fun onTick(millisUntilFinished: Long) {
                    if(timeFrame == 3600000L) {
                        val hour = millisUntilFinished / timeFrame % 24
                        primaryCountdownText.text = "In about $hour hours"
                    } else {
                        val day = millisUntilFinished / timeFrame
                        primaryCountdownText.text = "In about $day days"
                    }
                }

                override fun onFinish() {
                    primaryCountdownText.text = "happening now"
                }

            }

            countDownTimer.start()

        }
    }

}
