package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
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

        // Display user name dynamically from SharedPreferences for persistence
        val sharedPref = getSharedPreferences("SafePrefs", MODE_PRIVATE)
        val savedName = sharedPref.getString("USER_NAME", "User")
        findViewById<TextView>(R.id.tvHello).text = getString(R.string.hello_user, savedName)

        setupClickListeners()
        highlightHome()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardData()
    }

    private fun highlightHome() {
        findViewById<android.widget.ImageView>(R.id.ivNavHome).setColorFilter(android.graphics.Color.parseColor("#3B82F6"))
        findViewById<TextView>(R.id.tvNavHome).setTextColor(android.graphics.Color.parseColor("#3B82F6"))
    }

    private fun updateDashboardData() {
        // Update Stats using real data from Database
        dbHelper.getScanStats().apply {
            val total = get("total") ?: 0
            val safe = get("safe") ?: 0
            val threats = get("threats") ?: 0

            findViewById<TextView>(R.id.tvTotalScansCount).text = total.toString()
            findViewById<TextView>(R.id.tvSafeScansCount).text = safe.toString()
            findViewById<TextView>(R.id.tvThreatsCount).text = threats.toString()
        }

        // Update Recent Scans
        dbHelper.getRecentScans(4).also { scans ->
            val rvRecentScans = findViewById<RecyclerView>(R.id.rvRecentScans)
            val layoutEmptyState = findViewById<View>(R.id.layoutEmptyState)

            if (scans.isEmpty()) {
                rvRecentScans.visibility = View.GONE
                layoutEmptyState.visibility = View.VISIBLE
            } else {
                rvRecentScans.visibility = View.VISIBLE
                layoutEmptyState.visibility = View.GONE
                rvRecentScans.layoutManager = LinearLayoutManager(this)
                rvRecentScans.adapter = RecentScanAdapter(scans)
            }
        }

        updateDailyTip()
    }

    private fun updateDailyTip() {
        val tips = listOf(
            "Always check the URL carefully. Phishers often use URLs that look similar to trusted sites.",
            "Look for 'https://' and the padlock icon before entering sensitive info.",
            "Avoid clicking on suspicious links in emails or text messages.",
            "Keep your app and operating system updated to protect against the latest threats.",
            "Use a strong, unique password for every online account.",
            "Enable Two-Factor Authentication (2FA) for an extra layer of security.",
            "Be cautious of emails that create a sense of urgency or fear.",
            "Public Wi-Fi is not always secure. Avoid accessing bank accounts on open networks.",
            "Never share your OTP (One-Time Password) with anyone.",
            "Check for typos and unusual formatting in emails claiming to be from big brands."
        )
        val randomTip = tips.random()
        findViewById<TextView>(R.id.tvTipDescription).text = randomTip
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.cardScanURL).setOnClickListener {
            startActivity(Intent(this, ScanWebsiteActivity::class.java))
        }

        findViewById<View>(R.id.cardScanQR).setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

        findViewById<View>(R.id.fabScan).setOnClickListener {
            startActivity(Intent(this, ScanWebsiteActivity::class.java))
        }

        findViewById<View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, ScanHistoryActivity::class.java))
        }

        findViewById<View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<View>(R.id.navStats).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        findViewById<View>(R.id.btnViewAllRecent).setOnClickListener {
            startActivity(Intent(this, ScanHistoryActivity::class.java))
        }

        findViewById<View>(R.id.btnViewAllOverview).setOnClickListener {
            startActivity(Intent(this, ScanHistoryActivity::class.java))
        }

        findViewById<View>(R.id.btnProfileToolbar).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
