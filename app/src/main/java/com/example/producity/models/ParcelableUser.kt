package com.example.producity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// To pass as an argument between fragments using Safe Args
@Parcelize
data class ParcelableUser(
    val username: String,
    val uid: String,
    val displayName: String,
    val telegramHandle: String,
    val gender: String,
    val birthday: String,
    val bio: String,
    val imageUrl: String
) : Parcelable {
}