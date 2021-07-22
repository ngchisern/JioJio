package com.example.producity.ui.explore


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.databinding.FragmentExploreBinding
import com.example.producity.models.Activity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val exploreViewModel: ExploreViewModel by activityViewModels {
        ExploreViewModelFactory(
            ServiceLocator.provideParticipantRepository(),
            ServiceLocator.provideActivityRepository()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Add the RecyclerView
        exploreViewModel.setUp(sharedViewModel.getUser().username)

        val recyclerView = binding.taskRecyclerView
        val adapter = ExploreListAdapter(this, exploreViewModel)
        recyclerView.adapter = adapter
        val manager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = manager


        exploreViewModel.exploreActivities.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                Picasso.get()
                    .load("https://cdn.dribbble.com/users/1121009/screenshots/11030107/media/25be2b86a12dbfd8da02db4cfcbfe50a.jpg?compress=1&resize=400x300")
                    .into(binding.emptyExplore)
                binding.emptyExploreText.text =
                    "Whoops! No results found. \n Create an activity yourself!"
                view.setBackgroundColor(Color.parseColor("#FFFFFF"))
                adapter.submitList(it)
                return@observe
            }

            binding.emptyExploreText.text = ""
            binding.emptyExplore.setImageDrawable(null)

            view.setBackgroundColor(Color.parseColor("#f5f7ff"))
            adapter.submitList(it)
        }

        var loading = true
        var exist = true

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy >= 0) {
                    val visible = manager.childCount
                    val total = manager.itemCount
                    val past = manager.findFirstCompletelyVisibleItemPosition()

                    if (loading && exist) {
                        if (visible + past >= total) {
                            loading = false
                            val db = Firebase.firestore
                            val count = 2

                            db.collection("activity")
                                .orderBy("date")
                                .startAfter(exploreViewModel.latest)
                                .limit(count.toLong())
                                .get()
                                .addOnSuccessListener {
                                    val list = it.toObjects(Activity::class.java)

                                    if (list.isEmpty()) exist = false

                                    list.forEach { x ->
                                        exploreViewModel.latest = x.date
                                        if (x.participant.contains(sharedViewModel.getUser().username)) return@forEach

                                        exploreViewModel.allActivities.add(x)
                                        adapter.notifyItemInserted(exploreViewModel.allActivities.size - 1)
                                    }

                                    loading = true
                                }
                        }
                    }
                }
            }
        })


        listen()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        binding.welcomeNoti.setOnClickListener {
            val action = ExploreFragmentDirections.actionNavigationExploreToNotificationFragment()
            findNavController().navigate(action)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) return false

                exploreViewModel.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == "") {
                    exploreViewModel.getAll()
                }

                return false
            }

        })


    }

}