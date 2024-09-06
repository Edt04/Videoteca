package com.example.videoteca



import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoteca.databinding.CardFilmBinding
import java.time.LocalDate

class FilmAdapter(private var films: List<Film>,private var db :MovieDatabaseHelper, private val context: Context) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {
    inner class FilmViewHolder(val binding: CardFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedFilm = films[position]
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("title", selectedFilm.title)
                        putExtra("genre", selectedFilm.genre)
                        putExtra("year", selectedFilm.year)
                        putExtra("imageUrl", selectedFilm.imageUrl)
                        putExtra("description", selectedFilm.description)
                    }
                    context.startActivity(intent)
                }
            }
        }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = CardFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {

        val film = films[position]
        holder.binding.movieTitleTextView.text = film.title
        holder.binding.movieGenreTextView.text = film.genre
        holder.binding.movieYearTextView.text = film.year.toString()
        Log.d("FilmAdapter", "Loading image from URL: ${film.imageUrl}")
        Glide.with(context)
            .load(film.imageUrl) // Assicurati che questo sia il campo giusto e che contenga l'URL
            .placeholder(R.drawable.download) // Immagine mostrata durante il caricamento
            .error(R.drawable.error) // Immagine mostrata in caso di errore nel caricamento
            .into(holder.binding.movieImageView)
        // Cambia il colore del bottone in base allo stato del film
        if (film.state) {
            holder.binding.rentButton.text = "AVAILABLE"
            holder.binding.rentButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.binding.rentButton.text = "BUSY"
            holder.binding.rentButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        }

        // Gestione del clic sul pulsante "Noleggia"
        holder.binding.rentButton.setOnClickListener {
            if (film.state) {
                // Logica per noleggiare il film
                val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val username = sharedPref.getString("username", null)
                if(username!=null) {
                    db.insertRentedMovie(
                        username, holder.binding.movieTitleTextView.toString(),LocalDate.now().toString()
                    )
                    Toast.makeText(context, "Hai noleggiato ${film.title}", Toast.LENGTH_SHORT).show()

                    // Aggiorna lo stato del film e il colore del bottone
                    film.state = false
                    db.changeState(film.title, film.state)
                    notifyItemChanged(position)
                }
            } else {
                Toast.makeText(context, "${film.title} non è disponibile per il noleggio", Toast.LENGTH_SHORT).show()
            }
        }
        }


    override fun getItemCount() = films.size

    fun setMovies(movies: List<Film>) {
        this.films = movies
        notifyDataSetChanged()
    }
}
