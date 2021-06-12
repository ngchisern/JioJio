package com.example.producity.models

class Activity(val imageUrl: String, val title: String, val owner: String,
               val time: String, val pax: Int, val viewers: List<String>) {

    constructor() : this("","", "", "", -1, listOf())

}