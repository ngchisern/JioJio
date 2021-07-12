package com.example.producity.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Activity(val docId: String = "No doc Id",
                    val title: String = "No Title",
                    val owner: String = "No Owner",
                    @field:JvmField
                    val isPublic: Boolean = false,
                    @field:JvmField
                    val isVirtual: Boolean = false,
                    val date: Date = Date(2021-1900,1-1,1,12,0),
                    val location: String = "Delhi",
                    val pax: Int = 1,
                    val description: String = "No Description",
                    val participant: List<String> = listOf(),
                    val viewers: List<String> = listOf()
                    ) : Parcelable