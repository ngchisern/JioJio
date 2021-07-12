package com.example.producity.models

class Notification (val username: String = "",
                    val notiType: Int = -1 ,
                    val hasAccept: Boolean? = null,
                    val timestamp: Long = -1,
                    val docId: String? = "") {

    companion object {
        const val FRIENDREQUEST = 0 
        const val INVITE = 1
    }

}

