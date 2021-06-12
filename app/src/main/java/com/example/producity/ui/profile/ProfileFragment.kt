package com.example.producity.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.producity.LoginActivity
import com.example.producity.R
import com.example.producity.databinding.FragmentProfileBinding
import com.example.producity.models.Profile
import com.example.producity.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val pic: ImageView = binding.profilePic
//        val name: TextView = binding.name
//        val username: TextView = binding.username
//        val bio: TextView = binding.bio
//
//        setTextViews(name, username, bio)

        return root
    }

    private fun setTextViews(nameTV: TextView, usernameTV: TextView, bioTV: TextView) {
        val currentUser = auth.currentUser

        db.collection("users/")
            .whereEqualTo("uid", currentUser!!.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                it.forEach { doc ->
                    val temp = doc.toObject(User::class.java)
                    val username = temp.username

                    db.collection("users/$username/profile")
                        .limit(1)
                        .get()
                        .addOnSuccessListener {
                            it.forEach { doc ->
                                val profile = doc.toObject(Profile::class.java)
                                nameTV.text = profile.name
                                usernameTV.text = profile.username
                                bioTV.text = profile.bio
                            }
                        }
                }
            }
    }

}