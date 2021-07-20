package com.example.producity.ui.notification

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.producity.models.Notification
import com.example.producity.models.Participant
import com.example.producity.models.Request
import com.example.producity.models.User
import com.example.producity.models.source.IActivityRepository
import com.example.producity.models.source.IUserRepository
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class NotificationViewModel(val userRepository: IUserRepository): ViewModel() {
    private val rtdb = Firebase.database

    fun getUpdate(username: String): MutableLiveData<List<Notification>> {

        val result: MutableLiveData<List<Notification>> = MutableLiveData(listOf())

        rtdb.reference.child("notification/$username")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Notification>()
                    snapshot.children.forEach {
                        val temp = it.getValue(Notification::class.java) ?: return
                        list.add(temp)
                    }

                    result.value = list.reversed()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        return result

    }

    fun getRequest(username: String): MutableLiveData<List<Request>> {
        val result: MutableLiveData<List<Request>> = MutableLiveData(listOf())

        rtdb.reference.child("request/$username")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Request>()
                    snapshot.children.forEach {
                        val temp = it.getValue(Request::class.java) ?: return
                        list.add(temp)
                    }
                    result.value = list.reversed()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Main", "failed to update log")
                }
            })
        return result

    }

    fun loadImage(username: String, view: ImageView) {
        val storage = Firebase.storage

        storage.getReference("profile_pictures/${username}")
            .downloadUrl
            .addOnSuccessListener {  uri ->
                if(uri == null) return@addOnSuccessListener

                Picasso.get().load(uri).into(view)
            }
    }

    fun loadNickname(username: String, view: TextView) {
        val rtdb = Firebase.database

        rtdb.getReference("participant/$username")
            .get()
            .addOnSuccessListener {
                if(!it.exists()) return@addOnSuccessListener

                view.text = it.getValue(Participant::class.java)!!.nickname
            }
    }

    suspend fun checkUserExists(username: String): User? {
        val deferredBoolean: Deferred<User?> = viewModelScope.async {
            userRepository.checkUserExists(username)
        }
        return deferredBoolean.await()
    }


}

@Suppress("UNCHECKED_CAST")
class NotificationViewModelFactory(private val userRepository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (NotificationViewModel(userRepository) as T)
    }
}