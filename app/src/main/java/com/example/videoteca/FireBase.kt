package com.example.videoteca

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class FireBase {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Aggiungi un film
    fun addFilm(title: String, genre: String, year: Int, imageUrl: String, description: String) {
        val film = hashMapOf(
            "title" to title,
            "genre" to genre,
            "year" to year,
            "imageUrl" to imageUrl,
            "description" to description
        )

        db.collection("movies")
            .add(film)
            .addOnSuccessListener {
                Log.d("FirestoreHelper", "Film added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreHelper", "Error adding film", e)
            }
    }

    // Elimina un film
    fun deleteFilm(title: String, onComplete: (Boolean) -> Unit) {
        db.collection("movies")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onComplete(false)
                } else {
                    for (document in result) {
                        db.collection("movies").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                onComplete(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirestoreHelper", "Error deleting film", e)
                                onComplete(false)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreHelper", "Error finding film", e)
                onComplete(false)
            }
    }

    // Ottieni tutti i film
    fun getAllMovies(onSuccess: (QuerySnapshot) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("movies")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // Cerca film per titolo
    fun searchMovies(query: String, onSuccess: (QuerySnapshot) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("movies")
            .whereEqualTo("title", query)
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // Carica il profilo utente (se necessario)
    fun loadUserProfile(username: String, onSuccess: (DocumentSnapshot) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    onSuccess(document)
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // Aggiorna la password dell'utente
    fun updatePassword(username: String, newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    document.reference.update("password", newPassword)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


    // Logout utente
    fun logoutUser(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            auth.signOut()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
