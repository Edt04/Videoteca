package com.example.videoteca


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    lateinit var db : DatabaseHelper
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var passwordConfirm : EditText
    lateinit var email : EditText
    lateinit var registerBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = DatabaseHelper(this)
        username = findViewById(R.id.username)
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        passwordConfirm = findViewById(R.id.editTextConfirmPassword)
        registerBtn = findViewById(R.id.register_btn)

        registerBtn.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()
            val confirmPasswordText = passwordConfirm.text.toString()
            val emailText = email.text.toString()

            // Controllo se i campi sono vuoti
            if (usernameText.isEmpty() || passwordText.isEmpty() || emailText.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()

                // Controllo che le password coincidano
            } else if (passwordText != confirmPasswordText) {
                Toast.makeText(this, "The passwords are not the same", Toast.LENGTH_SHORT).show()

                // Controllo che l'email contenga il simbolo "@"
            } else if (!emailText.contains("@")) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()

                // Controllo se l'username o l'email esistono già nel database
            } else if (db.userExists(usernameText, emailText)) {
                Toast.makeText(this, "Username or email already exists", Toast.LENGTH_SHORT).show()

                // Se tutto è valido, inserisco l'utente nel database
            } else {
                val isInserted = db.insertData(usernameText, passwordText, emailText)
                if (isInserted) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
