package com.example.blacksquare.Models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class Kit(
    @field:Json(name ="title")
    private val title: String,
    @field:Json(name ="imageUrl")
    private val imageUrl: String,
    @field:Json(name ="kitDescription")
    private val kitDescription: String,
    @field:Json(name ="kitPrice")
    private val kitPrice: String)