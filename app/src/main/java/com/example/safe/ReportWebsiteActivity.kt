package com.example.safe

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ReportWebsiteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_website)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reportHeader)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val url = intent.getStringExtra("URL") ?: ""
        findViewById<EditText>(R.id.etReportUrl).setText(url)

        setupReasonSpinner()

        findViewById<ImageView>(R.id.btnBackReport).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnCancelReport).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnSubmitReport).setOnClickListener {
            Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupReasonSpinner() {
        val reasons = arrayOf("Phishing", "Malware", "Spam", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, reasons)
        val spinner = findViewById<AutoCompleteTextView>(R.id.spinnerReason)
        spinner.setAdapter(adapter)
    }
}