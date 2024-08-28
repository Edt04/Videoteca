package com.example.videoteca



import android.content.Context
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
        holder.binding.textViewTitle.text = film.title
        holder.binding.textViewGenre.text = film.genre
        holder.binding.textViewYear.text = film.year.toString()

        Glide.with(context)
            .load(film.imageResId)
            .into(holder.binding.imageViewPoster)
    }

    override fun getItemCount() = films.size

    fun setMovies(movies: List<Film>) {
        this.films = movies
        notifyDataSetChanged()
    }
}
