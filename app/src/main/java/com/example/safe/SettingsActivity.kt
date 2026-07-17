package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvSettingsTitle)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigation()
        setupSettingsClickListeners()
        
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupSettingsClickListeners() {
        findViewById<View>(R.id.btnPrivacy).setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
        findViewById<View>(R.id.btnTerms).setOnClickListener {
            startActivity(Intent(this, TermsOfServiceActivity::class.java))
        }
        findViewById<View>(R.id.btnAbout).setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }
        findViewById<View>(R.id.btnFeedback).setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
        findViewById<View>(R.id.btnHelp).setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }
    }

    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, ScanHistoryActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navStats).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.fabScan).setOnClickListener {
            startActivity(Intent(this, ScanWebsiteActivity::class.java))
        }
    }
}