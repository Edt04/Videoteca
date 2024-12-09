package com.example.videoteca

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoteca.databinding.CardFilmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FilmAdapter(
    private var films: List<Film>,
    private val context: Context
) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance() // Firebase Auth

    inner class FilmViewHolder(val binding: CardFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedFilm = films[position]
                    navigateToDetail(selectedFilm)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = CardFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        holder.binding.movieTitleTextView.text = film.title
        holder.binding.movieGenreTextView.text = film.genre
        holder.binding.movieYearTextView.text = film.year.toString()

        Glide.with(context)
            .load(film.imageUrl)
            .placeholder(R.drawable.download)
            .error(R.drawable.error)
            .into(holder.binding.movieImageView)

        // Imposta il testo e il colore del pulsante in base alla disponibilità del film
        if (film.state) {
            holder.binding.rentButton.text = "AVAILABLE"
            holder.binding.rentButton.setBackgroundColor(
                ContextCompat.getColor(context, R.color.green)
            )
        } else {
            holder.binding.rentButton.text = "BUSY"
            holder.binding.rentButton.setBackgroundColor(
                ContextCompat.getColor(context, R.color.red)
            )
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (email != null) {
                isAdmin(email) { isAdmin ->
                    if (isAdmin) {
                        // Disabilita il pulsante per gli admin
                        holder.binding.rentButton.isEnabled = false
                        holder.binding.rentButton.alpha = 0.5f // Riduci l'opacità per indicare che è disabilitato
                    } else {
                        // Abilita il pulsante e aggiungi la logica del noleggio per gli utenti non admin
                        holder.binding.rentButton.isEnabled = true
                        holder.binding.rentButton.alpha = 1.0f // Ripristina l'opacità
                        holder.binding.rentButton.setOnClickListener {
                            if (film.state) {
                                val currentTimestamp = System.currentTimeMillis()
                                val expiryTimestamp = currentTimestamp + 20 * 24 * 60 * 60 * 1000 // 20 giorni

                                val rentalData = hashMapOf(
                                    "email" to email,
                                    "title" to film.title,
                                    "imageUrl" to film.imageUrl,
                                    "rentalDate" to currentTimestamp,
                                    "expiryDate" to expiryTimestamp
                                )

                                db.collection("rentals")
                                    .add(rentalData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Hai noleggiato ${film.title}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Aggiorna lo stato del film in Firestore
                                        db.collection("movies").document(film.id)
                                            .update("state", false)
                                            .addOnSuccessListener {
                                                film.state = false
                                                notifyItemChanged(position)
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("FilmAdapter", "Errore aggiornando lo stato del film", e)
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Errore durante il noleggio",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e("FilmAdapter", "Errore registrando il noleggio", e)
                                    }
                            } else {
                                Toast.makeText(context, "${film.title} non è disponibile", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }


    override fun getItemCount() = films.size

    fun setMovies(movies: List<Film>) {
        this.films = movies
        notifyDataSetChanged()
    }

    // Naviga al dettaglio
    private fun navigateToDetail(film: Film) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra("title", film.title)
            putExtra("genre", film.genre)
            putExtra("year", film.year)
            putExtra("imageUrl", film.imageUrl)
            putExtra("description", film.description)
        }
        context.startActivity(intent)
    }
    // Funzione che verifica se l'email è presente nella raccolta admin
    private fun isAdmin(email: String, callback: (Boolean) -> Unit) {
        db.collection("admin")
            .whereEqualTo("email", email)  // Ricerca per email nella raccolta "admin"
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    callback(false)  // L'utente non è trovato, non è un admin
                } else {
                    callback(true)  // L'utente è trovato, è un admin
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Error checking admin status", e)
                callback(false)  // In caso di errore, si assume che l'utente non sia un admin
            }
    }
}
