package com.example.producity.models


data class Request(val code: Int = -1,
                   val requester: String = "",
                   val subject: String = "",
                   val timestamp: Long = -1 ) {

    companion object {
        val FRIENDREQUEST = 0
        val EVENTREQUEST = 1
    }

}