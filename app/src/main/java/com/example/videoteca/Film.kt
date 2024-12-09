package com.example.videoteca



data class Film(
    var id : String,
    val title: String,
    val genre: String,
    val year: Int,
    val imageUrl: String,
    val description:String,
    var state: Boolean
){

        // Costruttore vuoto richiesto da Firestore
        constructor() : this("", "", "", 0, "", "", true)
    }


