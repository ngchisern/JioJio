package com.example.producity.ui.myactivity.myactivitydetail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.MainActivity
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.databinding.ActivityDetailManageBinding
import com.example.producity.databinding.MyActivityDetailBinding
import com.example.producity.models.Activity
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModelFactory
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.okhttp.Dispatcher
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MyActivityManageFragment: Fragment() {
    private val myActivityDetailViewModel: MyActivityDetailViewModel by activityViewModels() {
        MyActivityDetailViewModelFactory(ServiceLocator.provideParticipantRepository(), ServiceLocator.provideActivityRepository())
    }

    private var _binding: ActivityDetailManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ActivityDetailManageBinding.inflate(inflater, container, false)

        val root: View = binding.root


        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = myActivityDetailViewModel.currentActivity!!

        val recycleView = binding.participantRecyclerView

        val manager = LinearLayoutManager(context, RecyclerView.VERTICAL,  true)
        val adapter = ParticipantlAdapter(this, true, true, myActivityDetailViewModel)

        recycleView.layoutManager = manager
        recycleView.adapter = adapter

        myActivityDetailViewModel.participantList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        updateLayout(activity)
        listen()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    var selectedPhoto: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Log.d("Main", view.toString())

            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedPhoto)

            val bitmapDrawable = BitmapDrawable(bitmap)

            val imageView: ImageView = binding.activityDetailImage

            imageView.setImageDrawable(bitmapDrawable)
        }
    }

    private fun updateLayout(activity: Activity) {
        if (_binding == null) return

        val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

        _binding?.apply {
            myActivityDetailViewModel.loadActivityImage(activity.docId, activityDetailImage)
            activityDetailTitle.setText(activity.title)
            activityDetailDate.setText(dateFormat.format(activity.date))
            activityDetailTime.setText(timeFormat.format(activity.date))
            activityDetailDescription.setText(activity.description)
            activityDetailPax.setText(activity.pax.toString())

            if(activity.isVirtual) {
                toggleButton.isChecked = false
                activityLocationText.text = "Mode"
                activityDetailLocation.setText("Online")
            } else {
                toggleButton.isChecked = true
                activityDetailLocation.setText("")
            }

            /*
            if(!activity.isPublic) {
                activityEditLock.setBackgroundResource(R.drawable.ic_baseline_lock_24)
                isPublic = false
            } else {
                activityEditLock.setBackgroundResource(R.drawable.ic_baseline_lock_open_24)
            }

             */
        }

    }

    private var isVirtual = true
    private var isPublic = true

    private fun listen() {
        binding.apply {
            activityDetailImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }

            activityEditLock.setOnClickListener {
                 toggle()
            }

            activityDetailDate.setOnClickListener {
                showDateDialog()
                Toast.makeText(context, "Date", Toast.LENGTH_SHORT).show()
            }

            activityDetailTime.setOnClickListener {
                showTimeDialog()
                Toast.makeText(context, "Time", Toast.LENGTH_SHORT).show()
            }

            toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    isVirtual = false
                    activityLocationText.text = "Location"
                    activityDetailLocation.setText("")
                } else {
                    isVirtual = true
                    activityLocationText.text = "Location"
                    activityDetailLocation.setText("Online")
                }
            }

            topAppBar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.done -> {
                        update()
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> true
                }
            }

            topAppBar.setNavigationOnClickListener {
                navigateBack()
            }
        }
    }

    private fun toggle() {
        if(isPublic) {
            binding.activityEditLock.setBackgroundResource(R.drawable.ic_baseline_lock_24)
            isPublic = false
        } else {
            binding.activityEditLock.setBackgroundResource(R.drawable.ic_baseline_lock_open_24)
            isPublic = true
        }
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private var pyear = -1
    private var pmonth = -1
    private var pday = -1
    private var phour = -1
    private var pminute = -1

    private fun showTimeDialog() {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            { view, hourOfDay, minute ->
                phour = hourOfDay
                pminute = minute

                var stage = "am"
                if (hourOfDay >= 12) stage = "pm"

                var min = minute.toString()
                if(minute < 10) {
                    min = "0$minute"
                }

                val text = "${hourOfDay % 12}:$min $stage"

                binding.activityDetailTime.setText(text)
            }, hour, minute, false
        )

        mTimePicker.show()
    }

    private fun showDateDialog() {
        val datePicker: DatePickerDialog
        val currentDate = Calendar.getInstance()
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val month = currentDate.get(Calendar.MONTH)
        val year = currentDate.get(Calendar.YEAR)

        datePicker = DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            object: DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    pyear = year
                    pmonth = month
                    pday = dayOfMonth

                    val text = "$dayOfMonth/${month+1}/$year"

                    binding.activityDetailDate.setText(text)
                }
            }, year, month, day
        )

        datePicker.show()
    }

    private fun update(){
        if(selectedPhoto != null) {
            val storage = Firebase.storage
            val filename = UUID.randomUUID().toString()
            val ref = storage.getReference("/activity_images/$filename")

            ref.putFile(selectedPhoto!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                            Log.d(MainActivity.TAG, "FileLocation: $it")
                        }
                        .addOnFailureListener {
                            Log.d(MainActivity.TAG, it.message.toString())
                        }
                }
        }

        val activity = myActivityDetailViewModel.currentActivity!!

        binding.apply {
            val title = activityDetailTitle.text.toString()
            val location = activityDetailLocation.text.toString()
            val pax = activityDetailPax.text.toString().toInt()
            val description = activityDetailDescription.text.toString()
            val date = getDate()

            CoroutineScope(IO).launch {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocationName(location, 1)
                val lat = if(address.isEmpty()) -1.0 else address[0].latitude
                val long = if(address.isEmpty()) -1.0 else address[0].longitude

                val updatedActivity = Activity(activity.docId, title, title.toLowerCase(), activity.owner,
                    2, isVirtual, date, lat, long, pax, description, activity.participant, activity.label)

                myActivityDetailViewModel.updateActivity(updatedActivity)
                myActivityDetailViewModel.setActivity(updatedActivity)
            }
        }


    }

    private fun getDate(): Date {
        val date = myActivityDetailViewModel.currentActivity!!.date
        if (pyear == -1 ) {
            pyear = date.year
        }

        if(pmonth == -1) {
            pmonth = date.month
        }

        if(pday == -1) {
            pday = date.day
        }

        if(phour == -1) {
            phour = date.hours
        }

        if(pminute == -1) {
            pminute = date.minutes
        }
        return Date(pyear - 1900, pmonth, pday, phour, pminute)
    }


}