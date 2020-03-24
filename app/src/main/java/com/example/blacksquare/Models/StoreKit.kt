package com.example.blacksquare.Models

data class StoreKit(
    val title: String,
    val imageUrl: String,
    val kitShortDescription: String,
    val kitFullDescription: String,
    val kitPrice: String,
    val sale: Boolean,
    val previewUrl : String
)