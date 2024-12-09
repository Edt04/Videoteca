package com.example.videoteca

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoteca.databinding.RentalItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RentalAdapter(private val rentals: List<Rental>, private val context: Context) :
    RecyclerView.Adapter<RentalAdapter.RentalViewHolder>() {

    inner class RentalViewHolder(val binding: RentalItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalViewHolder {
        val binding = RentalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RentalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RentalViewHolder, position: Int) {
        val rental = rentals[position]
        holder.binding.rentalMovieTitle.text = rental.title
        holder.binding.rentalMovieExpiry.text = formatDate( rental.expiryDate)

        Glide.with(context)
            .load(rental.imageUrl)
            .placeholder(R.drawable.download)
            .error(R.drawable.error)
        .into(holder.binding.rentalMovieImage)
    }

    private fun formatDate(timestamp: Long): String {
        return if (timestamp > 0) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        } else {
            "Data non valida"
        }
    }




    override fun getItemCount() = rentals.size

}
