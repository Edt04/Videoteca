package com.example.videoteca

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoteca.databinding.CardFilmBinding

class FilmAdapter(private var films: List<Film>, private val context: Context) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    private var filteredFilms: List<Film> = films

    inner class FilmViewHolder(val binding: CardFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedFilm = filteredFilms[position]
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

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = filteredFilms[position]
        holder.binding.movieTitleTextView.text = film.title
        holder.binding.movieGenreTextView.text = film.genre
        holder.binding.movieYearTextView.text = film.year.toString()
        Log.d("FilmAdapter", "Loading image from URL: ${film.imageUrl}")
        Glide.with(context)
            .load(film.imageUrl)
            .placeholder(R.drawable.download)
            .error(R.drawable.error)
            .into(holder.binding.movieImageView)
    }

    override fun getItemCount() = filteredFilms.size

    fun filter(query: String) {
        filteredFilms = if (query.isEmpty()) {
            films
        } else {
            films.filter { it.title.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    fun updateFilms(newFilms: List<Film>) {
        films = newFilms
        filter("") // Reapply filter with empty query to update the view
    }

    fun updateFilms(newFilms: Unit) {
        TODO("Not yet implemented")
    }
}
