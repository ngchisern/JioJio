package com.example.producity.ui.explore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null

    private val binding get() = _binding!!

    private val exploreViewModel: ExploreViewModel by activityViewModels() {
        ExploreViewModelFactory(ServiceLocator.provideParticipantRepository(), ServiceLocator.provideActivityRepository())
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
        val recyclerView = binding.taskRecyclerView
        val adapter = ExploreListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())


        exploreViewModel.friendActivities.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        _binding!!.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search_bar -> {
                    true
                }
                R.id.filter -> {
                    true
                }
                R.id.friends_or_public -> {
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}