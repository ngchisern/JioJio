package com.example.producity.source

import android.net.Uri
import com.example.producity.RegisterActivity
import com.example.producity.models.User
import com.example.producity.models.source.remote.IUserRemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class FakeUserRemoteDataSource : IUserRemoteDataSource {

    // fake user
    private var user = User(
        "testUsername", "testUid", "testDisplayName",
        "test_tele", "Male", "2000-01-01",
        "testBio", RegisterActivity.BLANK_PROFILE_IMG_URL
    )

    // fake friend list
    private var friends = listOf(
        User("friend0", "uid0", "",
            "", "", "", "", ""),
        User("friend1", "uid1", "",
            "", "", "", "", "")
    )

    override fun createUser(username: String, uid: String) {
        TODO("Not yet implemented")
    }

    override fun isUsernmeTaken(username: String): Task<DocumentSnapshot> {
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