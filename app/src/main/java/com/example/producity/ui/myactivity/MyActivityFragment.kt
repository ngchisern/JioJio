package com.example.producity.ui.myactivity

import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
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
import com.google.android.material.shape.CornerFamily
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        myActivityViewModel.myActivityList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.primaryCountdownText.text = "Happening now"
                binding.primaryTitle.text = "Living ..."
                binding.primaryLocation.text = "Everywhere and anywhere"
                binding.primaryDescription.text = "Find joy in what we do!"
                binding.primaryTime.text = "Forever"
                binding.homePrimaryEvent.isClickable = false

                Picasso.get()
                    .load("https://media.tenor.com/images/0cc5aae09f18a8ad8400f6a1a03a2dc0/tenor.png")
                    .into(binding.emptyComingnextImage)
                binding.emptyComingnext.text = "Wow, such empty :("
                return@observe
            }

            binding.homePrimaryEvent.isClickable = true

            if (it.size == 1) {
                Picasso.get()
                    .load("https://media.tenor.com/images/0cc5aae09f18a8ad8400f6a1a03a2dc0/tenor.png")
                    .into(binding.emptyComingnextImage)
                binding.emptyComingnext.text = "Wow, such empty :("
            } else {
                binding.emptyComingnext.text = ""
                binding.emptyComingnextImage.setImageDrawable(null)
            }


            val subject = it[0]
            updatePrimaryActivity(subject)
            upcomingAdapter.submitList(it.subList(1, it.size))
        }


        val viewPager = binding.homePager
        val pagerAdapter = PagerAdapter(this, myActivityViewModel)
        viewPager.adapter = pagerAdapter

        val tabLayout = binding.tabLayout

        myActivityViewModel.recommended.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                val image = binding.emptyRecommendationImage
                image.shapeAppearanceModel = image.shapeAppearanceModel
                    .toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 20F)
                    .build()

                Picasso.get()
                    .load("https://massets.appsflyer.com/wp-content/uploads/2021/02/machine-learning-in-digital-marketing-featured-min.png")
                    .into(image)

                val str = String(Character.toChars(0x1F916)) + " " + getString(R.string.learning)
                binding.emptyRecommendationText.text = str
                return@observe
            }

            pagerAdapter.submitList(it)
        }

        var count = 0
        timer = Timer()
        timer.schedule(object : TimerTask() {

            override fun run() {
                // handle empty count
                if (pagerAdapter.itemCount == 0) return

                requireActivity().runOnUiThread {
                    viewPager.currentItem = count % pagerAdapter.itemCount
                }

                count++
            }

        }, 5000, 5000)

        TabLayoutMediator(tabLayout, viewPager) { _, _ ->

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
            if (url == "") return@observe

            Picasso.get().load(url).into(binding.profileImage)
        }

        sharedViewModel.currentUser.observe(viewLifecycleOwner) {
            binding.welcomeText.text = "Hello, ${it.nickname.toUpperCase(Locale.ROOT)}"
        }

    }

    private fun listen() {
        binding.homePrimaryEvent.setOnClickListener {
            val action = MyActivityFragmentDirections.actionNavigationHomeToScheduleDetailFragment(
                myActivityViewModel.getActivity(0)
            )
            findNavController().navigate(action)
        }

        binding.welcomeNoti.setOnClickListener {
            val action =
                MyActivityFragmentDirections.actionNavigationMyActivityToNotificationFragment()
            findNavController().navigate(action)
        }
    }

    private fun updatePrimaryActivity(subject: Activity) {
        binding.apply {

            primaryTitle.text = subject.title
            primaryDescription.text = subject.description
            primaryLocation.text = ""

            val format = SimpleDateFormat("d MMM yyyy hh:mm aaa", Locale.getDefault())
            primaryTime.text = format.format(subject.date)

            val difference = subject.date.time - Timestamp.now().toDate().time

            val timeFrame: Long = if (difference < 86400000) {
                3600000
            } else {
                86400000
            }

            val countDownTimer = object : CountDownTimer(difference, timeFrame) {
                override fun onTick(millisUntilFinished: Long) {
                    if (timeFrame == 3600000L) {
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

            val geocoder = Geocoder(requireContext(), Locale.getDefault())

            primaryLocation.text = if (subject.isVirtual) "Online" else {
                val address = geocoder.getFromLocation(subject.latitude, subject.longitude, 1)

                if (address.isEmpty()) {
                    "Unknown"
                } else {
                    address[0].getAddressLine(0)
                }
            }

            countDownTimer.start()

        }
    }

}
