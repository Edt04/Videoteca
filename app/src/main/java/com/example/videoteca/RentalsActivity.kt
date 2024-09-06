package com.example.videoteca



import android.content.Context
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
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)


        filmAdapter = FilmAdapter(emptyList(),dbHelper, this)

        // Configura RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = filmAdapter
        if(username!=null)
            loadRentedMovies(username)

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

    private fun loadRentedMovies(username:String) {
        try {
            val rentedMovies = dbHelper.getRentedFilmsByUser(username)
            filmAdapter.setMovies(rentedMovies)
        } catch (e: Exception) {
            Log.e("RentalsActivity", "Error loading rented movies", e)
        }
    }

    private fun searchRentedMovies(query: String) {
        try {
            val rentedMovies = dbHelper.searchRentedMovies(query) // Assicurati di implementare questo metodo
            filmAdapter.setMovies(rentedMovies)
        } catch (e: Exception) {
            Log.e("RentalsActivity", "Error searching rented movies", e)
        }
    }
}