package com.example.videoteca

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoteca.databinding.ActivityAddFilmBinding
import com.example.videoteca.databinding.ActivityDeleteFilmBinding

private lateinit var dbHelper: MovieDatabaseHelper
private lateinit var binding: ActivityDeleteFilmBinding
class DeleteFilmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MovieDatabaseHelper(this)
        binding.btnDelete.setOnClickListener {
            val title = binding.Title.text.toString()
            if (title.isNotEmpty()) {
                dbHelper.deleteMovie(title)
                Toast.makeText(this, "Film deleted!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}