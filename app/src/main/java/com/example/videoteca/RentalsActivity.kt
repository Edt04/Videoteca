package com.example.videoteca



import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoteca.databinding.ActivityNoleggiBinding

class RentalsActivity : AppCompatActivity() {
    private lateinit var dbHelper: MovieDatabaseHelper
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var binding: ActivityNoleggiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoleggiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MovieDatabaseHelper(this)
        filmAdapter = FilmAdapter(emptyList(), this)

        // Configura RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = filmAdapter

        // Carica i film noleggiati
        loadRentedMovies()

        // Configura SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchRentedMovies(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchRentedMovies(it) }
                return true
            }
        })
    }

    private fun loadRentedMovies() {
        try {
            val rentedMovies = dbHelper.getRentedMovies()
            filmAdapter.updateFilms(rentedMovies)
        } catch (e: Exception) {
            Log.e("RentalsActivity", "Error loading rented movies", e)
        }
    }

    private fun searchRentedMovies(query: String) {
        try {
            val rentedMovies = dbHelper.searchRentedMovies(query) // Assicurati di implementare questo metodo
            filmAdapter.updateFilms(rentedMovies)
        } catch (e: Exception) {
            Log.e("RentalsActivity", "Error searching rented movies", e)
        }
    }
}
