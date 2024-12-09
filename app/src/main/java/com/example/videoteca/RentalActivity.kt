package com.example.videoteca

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoteca.databinding.ActivityRentalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RentalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentalBinding
    private lateinit var adapter: RentalAdapter
    private val db = FirebaseFirestore.getInstance()
    private val rentals = mutableListOf<Rental>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RentalAdapter(rentals, this)
        binding.rentalRecyclerView.adapter = adapter
        binding.rentalRecyclerView.layoutManager = LinearLayoutManager(this)

        loadRentals()
    }

    private fun loadRentals() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Errore: Nessun utente loggato", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("rentals")
            .whereEqualTo("email", currentUser.email)
            .get()
            .addOnSuccessListener { result ->
                rentals.clear()
                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val dateRented = document.getLong("rentalDate") ?: 0L
                    val expiryDate = document.getLong("expiryDate") ?: 0L
                    rentals.add(Rental(currentUser.email.toString(), title, imageUrl, dateRented , expiryDate))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errore durante il caricamento dei noleggi", Toast.LENGTH_SHORT).show()
                Log.e("RentalActivity", "Errore caricando i noleggi", e)
            }
    }
}
