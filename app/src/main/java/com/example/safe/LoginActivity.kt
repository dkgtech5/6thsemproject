package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val loginStatus = dbHelper.loginUser(email, password)
                if (loginStatus == 1) { // Success
                    val userDetails = dbHelper.getUserDetails(email)
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    
                    // Store user data in SharedPreferences for persistence
                    val sharedPref = getSharedPreferences("SafePrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("USER_EMAIL", email)
                        putString("USER_NAME", userDetails?.get("full_name"))
                        apply()
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("USER_NAME", userDetails?.get("full_name"))
                    intent.putExtra("USER_EMAIL", userDetails?.get("email"))
                    intent.putExtra("USER_ID", userDetails?.get("id"))
                    startActivity(intent)
                    finish()
                } else if (loginStatus == 2) { // Not Verified
                    Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, OtpVerificationActivity::class.java)
                    intent.putExtra("EMAIL", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}