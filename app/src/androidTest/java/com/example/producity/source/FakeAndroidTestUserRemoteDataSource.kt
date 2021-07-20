package com.example.producity.source

import android.net.Uri
import com.example.producity.RegisterActivity
import com.example.producity.models.User
import com.example.producity.models.source.remote.IUserRemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class FakeAndroidTestUserRemoteDataSource : IUserRemoteDataSource {

    // fake user
    private var user = User(
        "testUsername", "testUid", "testNickname",
        "test_tele", 2, Date(0),
        "testBio"
    )

    // fake friend list
    private var friends = listOf(
        User("friend0", "uid0"),
        User("friend1", "uid1")
    )

    override fun createUser(username: String, uid: String) {
        TODO("Not yet implemented")
    }

    override fun isUsernmeTaken(username: String): Task<DocumentSnapshot> {
        TODO("Not yet implemented")
    }

    override suspend fun checkUserExists(username: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun loadUserProfile(username: String): User {
        return user
    }

    override suspend fun editUserProfile(editedUserProfile: User) {
        user = editedUserProfile
    }

    override suspend fun loadFriends(username: String): List<User> {
        return friends
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
        user = userProfile
        return user
    }

}