package com.example.producity.ui.addactivity

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
import com.example.producity.databinding.AddActivityBinding
import com.example.producity.models.Activity
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.myactivity.MyActivityViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class AddActivityFragment: Fragment() {

    private var _binding: AddActivityBinding? = null

    private val friendListViewModel: FriendListViewModel by activityViewModels()
    private val myActivityViewModel: MyActivityViewModel by activityViewModels()
    private lateinit var db: FirebaseFirestore

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddActivityBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false
        db = Firebase.firestore

        val floatingButton: View? = activity?.findViewById(R.id.floating_action_button)
        floatingButton?.isVisible = false

        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topNav: MaterialToolbar = requireView().findViewById(R.id.topAppBar)

        topNav.setNavigationOnClickListener {
            val action = AddActivityFragmentDirections.actionAddActivityFragmentToNavigationMyActivity()
            findNavController().navigate(action)
        }

        val imageButton: ImageButton = requireView().findViewById(R.id.add_photo_image_button)

        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        val pickTime: ImageButton = requireView().findViewById(R.id.choose_time)

        pickTime.setOnClickListener {
            showTimeDialog()
        }

        val pickDate: ImageButton = requireView().findViewById(R.id.choose_date)

        pickDate.setOnClickListener {
            showDateDialog()
        }

        val createButton: Button = requireView().findViewById(R.id.create_button)

        createButton.setOnClickListener {
            uploadImageForFirebaseStorage()
        }

        val radioGroup: RadioGroup = binding.modeRadioGroup
        val location: EditText = binding.locationInput

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.virtual_option -> {
                    Toast.makeText(context, "virtual", Toast.LENGTH_SHORT).show()
                    location.isVisible = false
                }
                R.id.in_person_option -> {
                    Toast.makeText(context, "in person", Toast.LENGTH_SHORT).show()
                    location.isVisible = true
                }
            }
        }

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

            val imageView: ImageView =
                requireView().findViewById(R.id.add_activity_image_view) ?: return

            Log.d(MainActivity.TAG, "${imageView.background}")
            imageView.setBackgroundDrawable(bitmapDrawable)

            Log.d(MainActivity.TAG, "${imageView.background}")
        }
    }


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

                    requireView().findViewById<EditText>(R.id.timeInput)
                        .setText(text)
                }
            }, hour, minute, false
        )

        mTimePicker.show()
    }


    private var pyear: Int = -1
    private var pmonth: Int = -1
    private var pday: Int = -1
    private var phour: Int = -1
    private var pminute: Int = -1


    @RequiresApi(Build.VERSION_CODES.N)
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

                    requireView().findViewById<EditText>(R.id.dateInput)
                        .setText(text)
                }
            }, year, month, day
        )

        datePicker.show()
    }

    private fun uploadImageForFirebaseStorage() {

        if (selectedPhoto == null) {
            addToDataBase("https://pin.it/4LZJkED")
            return
        }

        val storage = Firebase.storage
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/activity_images/$filename")

        ref.putFile(selectedPhoto!!)
            .addOnSuccessListener {
                Log.d(MainActivity.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(MainActivity.TAG, "FileLocation: $it")
                    addToDataBase(it.toString())
                }
            }

    }



    private fun addToDataBase(imageUrl: String) {
        val title: EditText = requireView().findViewById(R.id.titleInput)
        val pax: EditText = requireView().findViewById(R.id.paxInput)
        val isPublic: RadioButton = requireView().findViewById(R.id.public_option)
        val location: EditText = requireView().findViewById(R.id.location_input)
        val description: EditText = requireView().findViewById(R.id.description_edittext)
        val isVirtual: RadioButton = binding.virtualOption

        val user = friendListViewModel.currentUser

        val listOfFriends: MutableList<String> = mutableListOf()
        val src = friendListViewModel.allFriends.value ?: listOf()

        for (elem in src) {
            listOfFriends.add(elem.username)
        }


        val newActivity = Activity(
            imageUrl,
            title.text.toString(),
            user.value?.username.toString(),
            user.value?.imageUrl.toString(),
            isPublic.isChecked,
            isVirtual.isChecked,
            Date(pyear-1900, pmonth, pday, phour, pminute),
            location.text.toString(),
            pax.text.toString().toInt(),
            description.text.toString(),
            mutableListOf(user.value?.username.toString()),
            listOfFriends
        )


        db.collection("activity")
            .add(newActivity)
            .addOnSuccessListener {
                Log.d(MainActivity.TAG, "added a new activity")

                myActivityViewModel.addActivity(
                    newActivity
                )
                val action = AddActivityFragmentDirections.actionAddActivityFragmentToNavigationMyActivity()
                findNavController().navigate(action)
            }
            .addOnFailureListener {
                Log.d(MainActivity.TAG, it.toString())
            }
    }
}