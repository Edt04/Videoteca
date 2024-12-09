package com.example.videoteca

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoteca.databinding.ActivityAddFilmBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddFilmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFilmBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val genre = binding.genreEditText.text.toString().trim()
            val year = binding.yearEditText.text.toString().trim().toIntOrNull()
            val imageUrl = binding.imageUrlEditText.text.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()

            if (title.isNotEmpty() && genre.isNotEmpty() && year != null && imageUrl.isNotEmpty() && description.isNotEmpty()) {
                addFilmToFirestore(title, genre, year, imageUrl, description)
            } else {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun addFilmToFirestore(title: String, genre: String, year: Int, imageUrl: String, description: String) {
        val film = hashMapOf(
            "title" to title,
            "title_lower" to title.lowercase(),
            "genre" to genre,
            "year" to year,
            "imageUrl" to imageUrl,
            "description" to description
        )

        db.collection("movies")
            .add(film)
            .addOnSuccessListener {
                Toast.makeText(this, "Movie added successfully!", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding movie: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


