package com.example.myapplication

import android.net.Uri

data class Tour(
    val name: String = "",
    val stops: MutableList<Stop> = mutableListOf(),
    val images: MutableList<String> = mutableListOf()
)