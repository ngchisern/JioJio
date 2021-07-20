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

    private val friends = mutableListOf(
        User("friend0", "uid0"),
        User("friend1", "uid1")
    )

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
        return if (userData.contains(username)) {
            User(username = username)
        } else {
            null
        }
    }

    override suspend fun loadUserProfile(username: String): User {
        return User(username = username)
    }

    override suspend fun editUserProfile(editedUserProfile: User) {

    }

    override suspend fun loadFriends(username: String): List<User> {
        return friends
    }

    override suspend fun sendFriendRequest(sender: User, receiverUsername: String) {

    }

    override suspend fun addFriend(currUser: User, friend: User) {
        friends.add(friend)
    }

    override suspend fun deleteFriend(currUser: User, friend: User) {
        friends.remove(friend)
    }

    override suspend fun uploadImageToFirebaseStorage(imageUri: Uri, username: String): String {
        return ""
    }

    override suspend fun getProfilePicUrl(username: String): String {
        return ""
    }

    override suspend fun updateProfileInFriends(editedUserProfile: User, friendUsername: String) {

    }

    override suspend fun uploadImageToFirebaseStorageAndEditProfile(
        imageUri: Uri,
        userProfile: User
    ): User {
        return User()
    }

}