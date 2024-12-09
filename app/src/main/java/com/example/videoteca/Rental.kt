package com.example.videoteca

data class Rental(
    val email: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val rentalDate: Long = 0L,
    val expiryDate: Long = 0L
)



