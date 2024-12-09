package com.example.videoteca

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoteca.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            loadUserProfile(currentUser.uid)
        } else {
            Toast.makeText(this, "Utente non autenticato", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        }

        binding.changePasswordButton.setOnClickListener {
            val currentPassword = binding.currentPasswordEditText.text.toString()
            val newPassword = binding.newPasswordEditText.text.toString()
            val confirmNewPassword = binding.confirmNewPasswordEditText.text.toString()

            if (validatePasswordChange(currentPassword, newPassword, confirmNewPassword)) {
                changePassword(newPassword)
            }
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun loadUserProfile(userId: String) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("username") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"

                    binding.usernameTextView.text = "Username: $username"
                    binding.emailTextView.text = "Email: $email"
                } else {
                    Toast.makeText(this, "Utente non trovato", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errore durante il caricamento del profilo", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }

    private fun validatePasswordChange(currentPassword: String, newPassword: String, confirmNewPassword: String): Boolean {
        return when {
            currentPassword.isEmpty() -> {
                Toast.makeText(this, "Inserisci la password attuale", Toast.LENGTH_SHORT).show()
                false
            }
            newPassword.isEmpty() -> {
                Toast.makeText(this, "Inserisci la nuova password", Toast.LENGTH_SHORT).show()
                false
            }
            confirmNewPassword.isEmpty() -> {
                Toast.makeText(this, "Conferma la nuova password", Toast.LENGTH_SHORT).show()
                false
            }
            newPassword != confirmNewPassword -> {
                Toast.makeText(this, "Le nuove password non coincidono", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun changePassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password cambiata con successo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Errore durante il cambiamento della password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logout() {
        auth.signOut()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
