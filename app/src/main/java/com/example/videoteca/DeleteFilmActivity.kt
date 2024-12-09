package com.example.videoteca

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoteca.databinding.ActivityDeleteFilmBinding
import com.google.firebase.firestore.FirebaseFirestore

class DeleteFilmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteFilmBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDelete.setOnClickListener {
            val title = binding.Title.text.toString().trim()

            if (title.isNotEmpty()) {
                deleteFilmFromFirestore(title)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFilmFromFirestore(title: String) {
        db.collection("movies")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Film not found", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in result) {
                        db.collection("movies").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Film deleted!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error deleting film: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error finding film: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
