package com.example.producity.ui.createactivity

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.producity.R
import com.example.producity.SharedViewModel
import com.example.producity.databinding.CreateLocationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*


class CreateLocationFragment: Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val createActivityViewModel: CreateActivityViewModel by activityViewModels()

    private var _binding: CreateLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View {

        _binding = CreateLocationBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val bottomNav: View? = activity?.findViewById(R.id.nav_view)
        bottomNav?.isVisible = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listen()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen() {
        val geocoder = Geocoder(context, Locale.getDefault())

        binding.cancelText.setOnClickListener {
            popDiscardDialog()
        }

        binding.backIcon.setOnClickListener {
            navigateBack()
        }

        binding.checkText.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)


                val text = binding.createLocation.text.toString()
                if (text.isEmpty()) {
                    binding.locationInputlayout.error = "Please enter a location."
                    return@launch
                }

                val address = geocoder.getFromLocationName(text, 1)
                if (address.size == 0) {
                    binding.locationInputlayout.error = "Can't resolve the location"
                    return@launch
                }
                binding.locationLocality.text = address[0].getAddressLine(0)
                query(address[0].getAddressLine(0))
            }

        }

        binding.nextButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val text = binding.createLocation.text.toString()
                if(text.isEmpty()) {
                    binding.locationInputlayout.error = "Please enter a location."
                    return@launch
                }

                val address = geocoder.getFromLocationName(text,1)

                if(address.size == 0) {
                    binding.locationInputlayout.error = "Can't resolve the location"
                    return@launch
                }
                createActivityViewModel.updateLocation(address[0].latitude, address[0].longitude)
                navigateToPrivacy()
            }
        }

    }

    private fun navigateToPrivacy() {
        val action = CreateLocationFragmentDirections.actionCreateLocationFragmentToCreatePrivacyFragment()
        findNavController().navigate(action)
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

    private fun query(subject: String) {
        val googleImageUrl = "https://www.google.co.in/search?biw=1366&bih=675&tbm=isch&sa=1&ei=qFSJWsuTNc-wzwKFrZHoCw&q="
        val base = googleImageUrl + subject
        CoroutineScope(Dispatchers.IO).launch {
            val url =  getImage(base)
            CoroutineScope(Dispatchers.Main).launch {
                Picasso.get().load(url).into(binding.locationImage)
            }

        }
    }

    private fun getImage(url: String): String {
        var document: Document? = null

        Log.d("Main", url)
        try {
            document = Jsoup.connect(url).userAgent("Mozilla").get()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return document!!.select("img")[2].absUrl("src")

    }
    
}