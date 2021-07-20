package com.example.producity.ui.createactivity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.CreateDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreateDetailFragment: Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val createActivityViewModel: CreateActivityViewModel by activityViewModels()

    private var _binding: CreateDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View {

        _binding = CreateDetailBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listen()
    }

    var selectedPhoto: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedPhoto)
            val bitmapDrawable = BitmapDrawable(bitmap)

            Log.d("Main", selectedPhoto.toString())

            binding.activityImage.setBackgroundDrawable(bitmapDrawable)
            binding.addImageButton.visibility = View.GONE
            binding.editImageButton.visibility = View.VISIBLE
        }
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        binding.cancelText.setOnClickListener {
            popDiscardDialog()
        }

        binding.backIcon.setOnClickListener {
            navigateBack()
        }

        binding.createCard.setOnClickListener {
            done()
        }

        binding.addImageButton.setOnClickListener {
            choosePhoto()
        }

        binding.editImageButton.setOnClickListener {
            choosePhoto()
        }

        binding.createDatetime.setOnClickListener {
            showDateDialog()
        }
    }

    private fun done() {
        if(checkInput()) {
            val creator =sharedViewModel.getUser().username
            val title = binding.createTitle.text.toString()
            val description = binding.createDescription.text.toString()
            val pax = binding.createPax.text.toString().toInt()

            val image = InputImage.fromFilePath(requireContext(), selectedPhoto!!)

            createActivityViewModel.createActivity(selectedPhoto!!, title, creator, description, date!!, pax, image)

            findNavController().popBackStack(R.id.navigation_createActivity, false)
            findNavController().navigate(R.id.navigation_myActivity)
        }
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun popDiscardDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.discard_dialog, null)

        builder.setView(view)

        val discard: TextView = view.findViewById(R.id.discard_button)
        val remain: TextView = view.findViewById(R.id.remain_button)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        discard.setOnClickListener {
            dialog.dismiss()
            cancel()
        }

        remain.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun cancel() {
        createActivityViewModel.cleanUp()
        findNavController().popBackStack(R.id.navigation_createActivity, false)
        findNavController().navigate(R.id.navigation_myActivity)
    }

    private fun checkInput(): Boolean {
        var isCorrect = true

        if(binding.createTitle.text.toString().isEmpty()) {
            isCorrect = false
            binding.titleLayout.error = "Please choose a title for your activity"
        }

        if(binding.createPax.text.toString().isEmpty() || binding.createPax.text.toString().toInt() < 1) {
            isCorrect = false
            binding.paxLayout.error = "Please choose a valid pax for your activity."

        }

        if(date == null) {
            isCorrect = false
            binding.dateLayout.error = "Please choose a start date and time for your activity."
        }

        if(selectedPhoto == null) {
            isCorrect = false
            binding.addImageButton.setTextColor(Color.RED)
        }

        return isCorrect
    }

    private var pyear: Int = -1
    private var pmonth: Int = -1
    private var pday: Int = -1
    private var phour: Int = -1
    private var pminute: Int = -1
    private var date: Date? = null

    private fun showDateDialog() {
        val datePicker: DatePickerDialog
        val currentDate = Calendar.getInstance()
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val month = currentDate.get(Calendar.MONTH)
        val year = currentDate.get(Calendar.YEAR)

        datePicker = DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            { _, year, month, dayOfMonth ->
                pyear = year
                pmonth = month
                pday = dayOfMonth

                showTimeDialog()
            }, year, month, day
        )

        datePicker.show()
    }

    private fun showTimeDialog() {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            { _, hourOfDay, minute ->
                phour = hourOfDay
                pminute = minute

                val formatter  = SimpleDateFormat("d MMM 'at' hh:mm aaa", Locale.getDefault())
                date = Date(pyear -1900, pmonth, pday, phour, pminute)
                binding.createDatetime.setText(formatter.format(date))


            }, hour, minute, false
        )

        mTimePicker.show()
    }
}