package com.example.videoteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoteca.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var dbHelper: MovieDatabaseHelper
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MovieDatabaseHelper(this)
        filmAdapter = FilmAdapter(emptyList(),dbHelper, this)

        // Configura RecyclerView
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = filmAdapter

        // Carica i film dal database
        loadMovies()

        // Configura SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchMovies(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchMovies(it) }
                return true
            }
        })

        // Configura BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    loadMovies() // Ricarica tutti i film
                    true
                }
                R.id.bottom_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    true
                }
                R.id.bottom_news -> {
                    loadNewMovies() // Gli ultimi 10 film inseriti
                    true
                }
                R.id.bottom_rentals -> {
                    // Apri RentalsActivity
                    startActivity(Intent(this, RentalsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadMovies() {
        try {
            val movies = dbHelper.getAllMovies()
            filmAdapter.setMovies(movies)
        } catch (e: Exception) {
            Log.e("UserActivity", "Error loading movies", e)
        }
    }

    private fun searchMovies(query: String) {
        try {
            val movies = dbHelper.searchMovies(query)
            filmAdapter.setMovies(movies)
        } catch (e: Exception) {
            Log.e("UserActivity", "Error searching movies", e)
        }
    }

    private fun loadNewMovies() {
        try {
            val movies = dbHelper.getAllNewMovies()
            filmAdapter.setMovies(movies)
        } catch (e: Exception) {
            Log.e("UserActivity", "Error loading new movies", e)
        }
    }
}
