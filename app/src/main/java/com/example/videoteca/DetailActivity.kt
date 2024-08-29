package com.example.videoteca

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.videoteca.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ricevi i dati passati tramite Intent
        val title = intent.getStringExtra("title")
        val genre = intent.getStringExtra("genre")
        val year = intent.getIntExtra("year", 0)
        val imageUrl = intent.getStringExtra("imageUrl")
        val description = intent.getStringExtra("description")

        // Imposta i dati nella View
        binding.filmTitleTextView.text= title
        binding.filmGenreTextView.text = genre
        binding.filmYearTextView.text = year.toString()
        binding.filmDescriptionTextView.text = description

        // Carica l'immagine con Glide
        Glide.with(this)
            .load(imageUrl)
            .into(binding.filmImageView)
    }
}
