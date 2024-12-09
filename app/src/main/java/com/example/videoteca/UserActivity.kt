package com.example.videoteca

import android.content.Intent
import android.os.Bundle
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoteca.databinding.ActivityAdminBinding
import com.example.videoteca.databinding.ActivityUserBinding
import com.example.videoteca.databinding.CardFilmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class UserActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var binding: ActivityUserBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        filmAdapter = FilmAdapter(emptyList(), this) // Rimosso dbHelper
        val workRequest = PeriodicWorkRequestBuilder<RentalCheckWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)

        // Configura RecyclerView
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = filmAdapter

        // Carica i film da Firestore
        loadMovies()

        // Configura SearchView
        var searchJob: Job? = null

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchMovies(it) } // Cerca direttamente al submit
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel() // Cancella la ricerca precedente
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(300) // Attendi 300ms prima di effettuare la ricerca
                    newText?.let { searchMovies(it) }
                }
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
                R.id.bottom_rentals ->{
                    startActivity(Intent(this,RentalActivity::class.java))
                    true
                }

                else -> {
                    false
                }
            }

        }
        val genreButton = binding.genreButton
        val genres = listOf("Action", "Comedy", "Drama", "Horror", "Sci-Fi","Anime","Cartoon","Crime","Thriller")  // Aggiungi i generi che desideri

        genreButton.setOnClickListener {
            // Crea un PopupMenu con il contesto e il bottone
            val popupMenu = PopupMenu(this, genreButton)

            // Aggiungi gli elementi al menu
            val menu = popupMenu.menu
            genres.forEachIndexed { index, genre ->
                menu.add(0, index, index, genre)
            }

            // Gestisci la selezione di un genere
            popupMenu.setOnMenuItemClickListener { item ->
                val selectedGenre = genres[item.itemId]
                // Fai qualcosa con il genere selezionato, ad esempio filtrare i film per genere
                filterMoviesByGenre(selectedGenre)
                true
            }

            // Mostra il menu
            popupMenu.show()
        }


    }

    private fun loadMovies() {
        db.collection("movies")
            .get()
            .addOnSuccessListener { result ->
                val movies = result.map { document ->
                    Film(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        genre = document.getString("genre") ?: "",
                        year = document.getLong("year")?.toInt() ?: 0,
                        imageUrl = document.getString("imageUrl") ?: "",
                        description = document.getString("description") ?: "",
                        state = document.getBoolean("state") ?: true
                    )
                }
                filmAdapter.setMovies(movies)
            }
            .addOnFailureListener { e ->
                Log.e("UserActivity", "Error loading movies", e)
            }
    }


    private fun searchMovies(query: String) {
        val endQuery = (query + '\uf8ff').lowercase() // Carattere speciale per estendere il range
        db.collection("movies")
            .whereGreaterThanOrEqualTo("title_lower", query.lowercase())
            .whereLessThanOrEqualTo("title_lower", endQuery)
            .get()
            .addOnSuccessListener { result ->
                val movies = result.map { document ->
                    Film(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        genre = document.getString("genre") ?: "",
                        year = document.getLong("year")?.toInt() ?: 0,
                        imageUrl = document.getString("imageUrl") ?: "",
                        description = document.getString("description") ?: "",
                        state = document.getBoolean("state") ?: true
                    )
                }
                filmAdapter.setMovies(movies)
            }
            .addOnFailureListener { e ->
                Log.e("SearchMovies", "Error searching movies", e)
            }
    }


    private fun loadNewMovies() {
        db.collection("movies")
            .orderBy("year", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val movies = result.map { document ->
                    Film(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        genre = document.getString("genre") ?: "",
                        year = document.getLong("year")?.toInt() ?: 0,
                        imageUrl = document.getString("imageUrl") ?: "",
                        description = document.getString("description") ?: "",
                        state = document.getBoolean("state") ?: true
                    )
                }
                filmAdapter.setMovies(movies)
            }
            .addOnFailureListener { e ->
                Log.e("UserActivity", "Error loading new movies", e)
            }
    }
    private fun filterMoviesByGenre(query : String){
        db.collection("movies")
            .whereEqualTo("genre", query)
            .get()
            .addOnSuccessListener { result ->
                val movies = result.map { document ->
                    Film(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        genre = document.getString("genre") ?: "",
                        year = document.getLong("year")?.toInt() ?: 0,
                        imageUrl = document.getString("imageUrl") ?: "",
                        description = document.getString("description") ?: "",
                        state = document.getBoolean("state") ?: true
                    )
                }
                filmAdapter.setMovies(movies)
            }
            .addOnFailureListener { e ->
                Log.e("FilterByGenre", "Error searching movies", e)
            }
    }

}