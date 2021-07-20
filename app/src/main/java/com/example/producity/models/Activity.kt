package com.example.producity.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Activity(
    val docId: String = "No doc Id",
    val title: String = "No Title",
    val lowerCaseTitle: String = "No title",
    val owner: String = "No Owner",
    val privacy: Int = -1,
    @field:JvmField
    val isVirtual: Boolean = false,
    val date: Date = Date(2021 - 1900, 1 - 1, 1, 12, 0),
    val latitude: Double = -1.0,
    val longitude: Double = -1.0,
    val pax: Int = 1,
    val description: String = "No Description",
    val participant: List<String> = listOf(),
    val label: List<String>? = listOf()
) : Parcelable {

    companion object {
        const val PRIVATE = 0
        const val PROTECTED = 1
        const val PUBLIC = 2

    }

}