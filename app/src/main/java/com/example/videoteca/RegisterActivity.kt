package com.example.videoteca

import DatabaseHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    lateinit var db : DatabaseHelper
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var passwordConfirm :EditText
    lateinit var email : EditText
    lateinit var registerBtn : Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = DatabaseHelper(this)
        username = findViewById(R.id.username)
        email = findViewById(R.id.editTextEmail)
        password =findViewById(R.id.editTextPassword)
        passwordConfirm = findViewById(R.id.editTextConfirmPassword)
        registerBtn = findViewById(R.id.register_btn)

        registerBtn.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()
            val confirmPassword = passwordConfirm.text.toString()
            val email =email.text.toString()

            if (username.isEmpty() || password.isEmpty()|| email.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else if(!password.equals(confirmPassword)) {
                Toast.makeText(this,"The passwords are not the same",Toast.LENGTH_SHORT).show()
            }
            else{
                val isInserted = db.insertData(username, password)
                if (isInserted) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}