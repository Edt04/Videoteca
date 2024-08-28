package com.example.videoteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.SearchView
import com.example.videoteca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: MovieDatabaseHelper
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MovieDatabaseHelper(this)
        filmAdapter = FilmAdapter(emptyList(), this)

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
                R.id.bottom_add -> {
                    startActivity(Intent(this, AddFilmActivity::class.java))
                    true
                }
                R.id.bottom_delete->{
                    startActivity(Intent(this,DeleteFilmActivity::class.java))
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
            Log.e("MainActivity", "Error loading movies", e)
        }
    }

    private fun searchMovies(query: String) {
        try {
            val movies = dbHelper.searchMovies(query)
            filmAdapter.setMovies(movies)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error searching movies", e)
        }
    }
}
