package com.example.producity.ui.myactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.producity.LoginActivity
import com.example.producity.R
import com.example.producity.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyActivityFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val myActivityViewModel: MyActivityViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //_binding = FragmentHomeBinding.inflate(inflater, container, false)
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)


        val adapter = MyActivityAdapter(this, myActivityViewModel)
        _binding?.scheduleRecycleView?.adapter = adapter
        binding.scheduleRecycleView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val root: View = binding.root

        auth = Firebase.auth
        db = Firebase.firestore

        _binding?.topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sign_out -> {
                    Log.d("Main", "sign out")
                    auth.signOut()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        myActivityViewModel.myActivityList.observe(viewLifecycleOwner) {
            myActivityViewModel.listInCharge = it
            myActivityViewModel.documentIdInCharge = myActivityViewModel.documentIds
            adapter.submitList(it)
        }


        binding.tabbar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener  {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d("Main", "clicked tab")
                when(tab?.position) {
                    0 -> {
                        adapter.submitList(myActivityViewModel.myActivityList.value)
                        myActivityViewModel.listInCharge = myActivityViewModel.myActivityList.value!!
                        myActivityViewModel.documentIdInCharge = myActivityViewModel.documentIds
                        Log.d("Main", "clicked incoming tab")
                    }
                    1 -> {
                        adapter.submitList(myActivityViewModel.pastActivityList.value)
                        myActivityViewModel.listInCharge = myActivityViewModel.pastActivityList.value!!
                        myActivityViewModel.documentIdInCharge = myActivityViewModel.pastDocumentIds
                        Log.d("Main", "clicked  past tab")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        Log.d("TEST", "created")
        val floatingButton: View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = true

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


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    class DatePickerFragment(val viewModel: MyActivityViewModel) : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            return DatePickerDialog(this.requireContext(), this, year, month, day)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            viewModel.updateDate(year, month + 1, day)
        }
    }

    fun popTimePicker() {
        val newFragment = DatePickerFragment(myActivityViewModel)
        newFragment.show(requireFragmentManager(), "dialog")
    }

     */

}
