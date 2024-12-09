package com.example.videoteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.SearchView
import com.example.videoteca.databinding.ActivityAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var filmAdapter: FilmAdapter
    private lateinit var binding: ActivityAdminBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filmAdapter = FilmAdapter(emptyList(), this)

        // Configura RecyclerView
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = filmAdapter

        // Carica i film da Firebase Firestore
        loadMovies()

        binding.recyclerView.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

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
                R.id.bottom_add -> {
                    startActivity(Intent(this, AddFilmActivity::class.java))
                    true
                }
                R.id.bottom_delete -> {
                    startActivity(Intent(this, DeleteFilmActivity::class.java))
                    true
                }
                R.id.bottom_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    true
                }
                else -> false
            }

        }
        val genres = listOf("Action", "Comedy", "Drama", "Horror", "Sci-Fi","Anime","Cartoon","Crime","Thriller")  // Aggiungi i generi che desideri

        binding.genreBtn.setOnClickListener {
            // Crea un PopupMenu con il contesto e il bottone
            val popupMenu = PopupMenu(this, binding.genreBtn)

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
                val movies = result.documents.mapNotNull { document ->
                    document.toObject(Film::class.java)?.apply { id = document.id }
                }
                filmAdapter.setMovies(movies)
            }
            .addOnFailureListener { e ->
                Log.e("AdminActivity", "Error loading movies", e)
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
