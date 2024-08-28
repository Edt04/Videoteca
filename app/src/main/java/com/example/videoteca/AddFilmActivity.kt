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
    private var selectedImageResId: Int = R.drawable.download // ID immagine di default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MovieDatabaseHelper(this)

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val genre = binding.etGenre.text.toString()
            val year = binding.etYear.text.toString().toIntOrNull() ?: 0

            if (title.isNotEmpty() && genre.isNotEmpty() && year > 0) {
                dbHelper.insertMovie(title, genre, year, selectedImageResId, "Default description")
                Toast.makeText(this, "Film added!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

