package com.example.videoteca


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoteca.databinding.ActivityAddFilmBinding

class AddFilmActivity : AppCompatActivity() {

    private lateinit var dbHelper: MovieDatabaseHelper
    private lateinit var binding: ActivityAddFilmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MovieDatabaseHelper(this)

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val genre = binding.genreEditText.text.toString().trim()
            val year = binding.yearEditText.text.toString().trim().toIntOrNull()
            val imageUrl = binding.imageUrlEditText.text.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()

            if (title.isNotEmpty() && genre.isNotEmpty() && year != null && imageUrl.isNotEmpty() && description.isNotEmpty()) {
                val success = dbHelper.addMovie(title, genre, year, imageUrl, description)
                if (success > 0) {
                    Toast.makeText(this, "Movie added successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error adding movie.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

