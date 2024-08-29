package com.example.videoteca



import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoteca.databinding.CardFilmBinding

class FilmAdapter(private var films: List<Film>, private val context: Context) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    inner class FilmViewHolder(val binding: CardFilmBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = CardFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

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
    }

    override fun getItemCount() = films.size

    fun setMovies(movies: List<Film>) {
        this.films = movies
        notifyDataSetChanged()
    }
}
