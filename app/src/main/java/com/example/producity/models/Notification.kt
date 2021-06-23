package com.example.producity.models

class Notification (val imageUrl: String = "https://i.picsum.photos/id/237/200/300.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U",
                         val message: String = "no message",
                        @field:JvmField
                         val isFriendRequest: Boolean = false,
                         val hasAccept: Boolean? = null,
                         val timestamp: Long = -1,
                         val docId: String = "activity")

