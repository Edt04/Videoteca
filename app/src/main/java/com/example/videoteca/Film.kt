package com.example.videoteca

import android.media.audiofx.AudioEffect.Descriptor


data class Film(
    val id : Int,
    val title: String,
    val genre: String,
    val year: Int,
    val imageUrl: String,
    val description:String
)
