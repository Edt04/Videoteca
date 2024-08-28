package com.example.videoteca

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var register: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = DatabaseHelper(this);
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        register = findViewById(R.id.rg_text)
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                val isUserExist = db.checkUser(username, password)
                if (isUserExist) {
                    if (username.toLowerCase().contains("admin")) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        // Crea un Intent per passare a SecondActivity
                        val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                        // Avvia il catalogo
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        // Crea un Intent per passare a SecondActivity
                        val intent = Intent(this@LoginActivity, UserActivity::class.java)
                        // Avvia il catalogo
                        startActivity(intent)
                        finish()

                    }
                }else{
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()

                }
            }
            register.setOnClickListener {
                // Crea un Intent per passare a SecondActivity
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                // Avvia la SecondActivity
                startActivity(intent)
            }
        }
    }
}