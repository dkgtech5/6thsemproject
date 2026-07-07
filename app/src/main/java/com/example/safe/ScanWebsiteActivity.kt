package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ScanWebsiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_website)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnClear = findViewById<ImageView>(R.id.btnClear)
        val btnAnalyze = findViewById<Button>(R.id.btnAnalyze)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnClear.setOnClickListener {
            etUrl.text.clear()
        }

        btnAnalyze.setOnClickListener {
            val url = etUrl.text.toString()
            if (url.isNotEmpty()) {
                val intent = Intent(this, ScanningActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
            }
        }
    }
}