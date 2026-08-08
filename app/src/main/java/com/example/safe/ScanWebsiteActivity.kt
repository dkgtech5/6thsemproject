package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanWebsiteActivity : AppCompatActivity() {
    private lateinit var etUrl: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAnalyze: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_website)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUrl = findViewById(R.id.etUrl)
        val btnClear = findViewById<ImageView>(R.id.btnClear)
        btnAnalyze = findViewById(R.id.btnAnalyze)
        progressBar = findViewById(R.id.progressBar)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnClear.setOnClickListener {
            etUrl.text.clear()
        }

        btnAnalyze.setOnClickListener {
            val url = etUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                val intent = Intent(this, ScanningActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup sample URL clicks to set text dynamically
        findViewById<View>(R.id.itemExample1).setOnClickListener { setUrl("https://esewa.com.np") }
        findViewById<View>(R.id.itemExample2).setOnClickListener { setUrl("https://khalti.com") }
        findViewById<View>(R.id.itemExample3).setOnClickListener { setUrl("http://esewa-kyc-update-login.com") }
        findViewById<View>(R.id.itemExample4).setOnClickListener { setUrl("http://khalti-verify-account.net") }
        findViewById<View>(R.id.itemExample5).setOnClickListener { setUrl("http://paypal-security-alert-update.com/login.php") }
        findViewById<View>(R.id.itemExample6).setOnClickListener { setUrl("http://192.168.1.1/login-facebook-verify.html") }
        findViewById<View>(R.id.itemExample7).setOnClickListener { setUrl("https://ncit.edu.np/") }
    }

    private fun setUrl(url: String) {
        etUrl.setText(url)
        etUrl.setSelection(url.length) // Move cursor to end
    }
}