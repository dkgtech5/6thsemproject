package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private val TAG = "SignUpActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        dbHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val fullName = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                if (dbHelper.checkEmail(email)) {
                    Toast.makeText(this, "Email already registered.", Toast.LENGTH_SHORT).show()
                } else {
                    val result = dbHelper.registerUser(fullName, email, password)
                    if (result != -1L) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "User registered: $fullName, ID: $result")
                        
                        // Print all users to Logcat for verification
                        val users = dbHelper.getAllUsers()
                        for (user in users) {
                            Log.d(TAG, "Stored User: ID=${user["id"]}, Name=${user["full_name"]}, Email=${user["email"]}")
                        }

                        navigateToLogin()
                    } else {
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Registration failed for email: $email")
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            navigateToMain()
        }

        findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            finish() // Go back to Login
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}