package com.example.videoteca
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class RentalCheckWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Chiama il metodo per controllare i noleggi scaduti
        checkAndRemoveExpiredRentals()
        return Result.success()
    }

    private fun checkAndRemoveExpiredRentals() {
        val db = FirebaseFirestore.getInstance()
        var currentTimestamp = System.currentTimeMillis()

        db.collection("rentals")
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    val expiryDate = document.getLong("expiryDate") ?: 0L
                    val movieTitle = document.getString("title")
                    val rentalId = document.id

                    if (currentTimestamp > expiryDate) {
                        db.collection("rentals").document(rentalId).delete()
                        db.collection("movies")
                            .whereEqualTo("title", movieTitle)
                            .get()
                            .addOnSuccessListener { movies ->
                                for (movie in movies.documents) {
                                    db.collection("movies").document(movie.id)
                                        .update("state", true)
                                }
                            }
                    }
                }
            }
        Log.d("RentalCheckWorker", "Controllo dei noleggi completato.")
    }
}
