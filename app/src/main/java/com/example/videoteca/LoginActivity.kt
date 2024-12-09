package com.example.videoteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoteca.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()  // Inizializza Firestore

        binding.loginBtn.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            isAdmin(username) { isAdmin ->
                                if (isAdmin) {
                                    startActivity(Intent(this, AdminActivity::class.java))
                                } else {
                                    startActivity(Intent(this, UserActivity::class.java))
                                }
                                finish()
                            }
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rgText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
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
