package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        
        dbHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            
            // Apply padding to bottom nav to extend background behind navigation bar
            val bottomNav = findViewById<View>(R.id.bottomNav)
            bottomNav.setPadding(0, 0, 0, systemBars.bottom)
            
            // Adjust FAB margin to stay floating at the same relative height
            val fabScan = findViewById<View>(R.id.fabScan)
            val params = fabScan.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            val baseMargin = (35 * resources.displayMetrics.density).toInt()
            params.bottomMargin = baseMargin + systemBars.bottom
            fabScan.layoutParams = params

            insets
        }

        loadUserProfile()
        setupNavigation()
        setupSettingsClickListeners()
        
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            // Clear preferences on logout
            val sharedPref = getSharedPreferences("SafePrefs", android.content.Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        highlightProfile()
    }

    private fun loadUserProfile() {
        val sharedPref = getSharedPreferences("SafePrefs", android.content.Context.MODE_PRIVATE)
        val email = sharedPref.getString("USER_EMAIL", "") ?: ""
        
        if (email.isNotEmpty()) {
            val userDetails = dbHelper.getUserDetails(email)
            val name = userDetails?.get("full_name") ?: "User"
            findViewById<TextView>(R.id.tvProfileName).text = name
            findViewById<TextView>(R.id.tvProfileEmail).text = email
            
            // Set Initial
            if (name.isNotEmpty()) {
                findViewById<TextView>(R.id.tvProfileInitial).text = name[0].uppercaseChar().toString()
            }
        }
    }

    private fun highlightProfile() {
        try {
            findViewById<ImageView>(R.id.ivNavProfile).setColorFilter(android.graphics.Color.parseColor("#3B82F6"))
            findViewById<TextView>(R.id.tvNavProfile).setTextColor(android.graphics.Color.parseColor("#3B82F6"))
        } catch (e: Exception) {
            e.printStackTrace()
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