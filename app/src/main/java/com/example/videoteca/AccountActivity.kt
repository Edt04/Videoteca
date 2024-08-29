package   com.example.videoteca

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoteca.LoginActivity
import com.example.videoteca.databinding.ActivityAccountBinding
import com.example.videoteca.databinding.ActivityAddFilmBinding


class AccountActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)




        if (username != null) {
            loadUserProfile(username)
        } else {
            Toast.makeText(this, "Username non trovato", Toast.LENGTH_SHORT).show()
            // Possibile reindirizzamento al LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.changePasswordButton.setOnClickListener {
            val currentPassword = binding.currentPasswordEditText.text.toString()
            val newPassword = binding.newPasswordEditText.text.toString()
            val confirmNewPassword = binding.confirmNewPasswordEditText.text.toString()

            if (validatePasswordChange(username.toString(), currentPassword, newPassword, confirmNewPassword)) {
                changePassword(username.toString(), newPassword)
            }
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun loadUserProfile(username: String) {
        val user = dbHelper.getUser(username)
        if (user != null) {
            binding.usernameTextView.text = "Username: ${user.username}"
            binding.emailTextView.text = "Email: ${user.email}"
            // Imposta un'immagine del profilo se disponibile
            // profileImageView.setImageResource(R.drawable.ic_profile_placeholder) // Placeholder
        } else {
            Toast.makeText(this, "Utente non trovato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validatePasswordChange(username: String, currentPassword: String, newPassword: String, confirmNewPassword: String): Boolean {
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
            !dbHelper.validatePassword(username, currentPassword) -> {
                Toast.makeText(this, "La password attuale è errata", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun changePassword(username: String, newPassword: String) {
        if (dbHelper.updatePassword(username, newPassword)) {
            Toast.makeText(this, "Password cambiata con successo", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Errore durante il cambiamento della password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("username")
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}