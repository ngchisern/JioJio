package com.example.producity.models.source

import android.net.Uri
import com.example.producity.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FakeTestUserRepository: IUserRepository {
    var userData = LinkedHashMap<String, String>()

    fun buildUserData(map: LinkedHashMap<String, String>) {
        userData = map
    }

    override fun createUser(username: String, uid: String) {
        userData[username] = uid
    }

    override fun isUsernameTaken(username: String): Boolean {
        return userData.contains(username)
    }

    override suspend fun checkUserExists(username: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun loadUserProfile(username: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun editUserProfile(editedUserProfile: User) {
        TODO("Not yet implemented")
    }

    override suspend fun loadFriends(username: String): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun sendFriendRequest(sender: User, receiverUsername: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addFriend(currUser: User, friend: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFriend(currUser: User, friend: User) {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun getProfilePicUrl(username: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun updateProfileInFriends(editedUserProfile: User, friendUsername: String) {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImageToFirebaseStorageAndEditProfile(
        imageUri: Uri,
        userProfile: User
    ): User {
        TODO("Not yet implemented")
    }

}