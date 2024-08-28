package com.example.videoteca


data class Film(
    val id: Int,
    val title: String,
    val genre: String,
    val year: Int,
    val imageResId: Int,
    val description: String
)
