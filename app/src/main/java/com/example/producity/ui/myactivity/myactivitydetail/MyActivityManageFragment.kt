package com.example.producity.ui.myactivity.myactivitydetail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
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
import com.example.producity.MainActivity
import com.example.producity.R
import com.example.producity.databinding.ActivityDetailManageBinding
import com.example.producity.databinding.MyActivityDetailBinding
import com.example.producity.models.Activity
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MyActivityManageFragment: Fragment() {
    private val myActivityViewModel: MyActivityViewModel by activityViewModels()
    private val friendViewModel: FriendListViewModel by activityViewModels()

    private var _binding: ActivityDetailManageBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ActivityDetailManageBinding.inflate(inflater, container, false)

        val root: View = binding.root


        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        val floatingButton: View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = false

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())

        val activity: Activity =
            myActivityViewModel.listInCharge.get(arguments.position)

        updateLayout(activity)

        listen()

    }

    override fun onDestroyView() {
        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = true
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
            Picasso.get().load(activity.imageUrl).into(activityDetailImage)
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
                activityDetailLocation.setText(activity.location)
            }

            if(!activity.isPublic) {
                activityEditLock.setBackgroundResource(R.drawable.ic_baseline_lock_24)
                isPublic = false
            } else {
                activityEditLock.setBackgroundResource(R.drawable.ic_baseline_lock_open_24)
            }
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

                    val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())
                    val activity: Activity = myActivityViewModel.listInCharge.get(arguments.position)
                    activityDetailLocation.setText(activity.location)
                } else {
                    isVirtual = true
                    activityLocationText.text = "Mode"
                    activityDetailLocation.setText("Online")
                }
            }

            topAppBar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.done -> {
                        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())

                        /*
                        db.document("activity/${myActivityViewModel.documentIdInCharge[arguments.position]}")
                            .update(update())
                            .addOnSuccessListener {
                                Log.d("Main", "Successfully Editted")
                            }
                            .addOnFailureListener {
                                Log.d("Main", it.message.toString())
                            }

                         */

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
        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())
        val action = MyActivityManageFragmentDirections.actionMyActivityManageFragmentToScheduleDetailFragment(arguments.position)
        findNavController().navigate(action)
    }

    private var isDateChanged = false
    private var pyear: Int = -1
    private var pmonth: Int = -1
    private var pday: Int = -1
    private var phour: Int = -1
    private var pminute: Int = -1

    private fun showTimeDialog() {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
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
                    isDateChanged = true
                }
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
                    isDateChanged = true
                }
            }, year, month, day
        )

        datePicker.show()
    }

    private fun update(): HashMap<String, Any> {
        val map = hashMapOf<String, Any>()

        val arguments = MyActivityDetailFragmentArgs.fromBundle(requireArguments())
        val activity: Activity = myActivityViewModel.listInCharge.get(arguments.position)


        if(selectedPhoto != null) {
            Log.d("Main", "imageUrl change")
            //map.put("imageUrl", putToStorage())
        }

        binding.apply {
            val title = activityDetailTitle.text.toString()
            if(!title.equals(activity.title)) {
                Log.d("Main", "title change")
                map.put("title", title)
            }

            val location = activityDetailLocation.text.toString()
            if(!location.equals(activity.location) && !isVirtual) {
                Log.d("Main", "location change")
                map.put("location", location)
            }

            val pax = activityDetailPax.text.toString().toInt()
            if(!pax.equals(activity.pax)) {
                Log.d("Main", "pax change $pax ${activity.pax}")
                map.put("pax", pax)
            }

            val description = activityDetailDescription.text.toString()
            if(!description.equals(activity.description)) {
                Log.d("Main", "dascription change")
                map.put("description", description)
            }

            if(!isVirtual.equals(activity.isVirtual)) {
                Log.d("Main", "isVirtual change")
                map.put("isVirtual", isVirtual)
            }

        }
        if(!isPublic.equals(activity.isPublic)) {
            Log.d("Main", "isPublic change")
            map.put("isPublic", isPublic)
        }


        if(isDateChanged) {
            Log.d("Main", "date change")
            map.put("date", Date(pyear-1900, pmonth, pday, phour, pminute))
        }


        return map
    }

    private fun putToStorage(): String {
        val storage = Firebase.storage
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/activity_images/$filename")
        var url: String = ""

        ref.putFile(selectedPhoto!!)
            .addOnSuccessListener {
                Log.d(MainActivity.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(MainActivity.TAG, "FileLocation: $it")
                    url = it.toString()
                }
            }

        return url
    }


}