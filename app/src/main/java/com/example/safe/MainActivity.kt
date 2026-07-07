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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecentScans()
        setupClickListeners()
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
    }

    private fun setupRecentScans() {
        val rvRecentScans = findViewById<RecyclerView>(R.id.rvRecentScans)
        val scans = listOf(
            RecentScan("google.com", "Today, 10:30 AM", true),
            RecentScan("secure-login.xyz", "Today, 09:15 AM", false),
            RecentScan("github.com", "Yesterday, 08:40 PM", true),
            RecentScan("amazon-offer.top", "Yesterday, 07:10 PM", false)
        )
        
        rvRecentScans.layoutManager = LinearLayoutManager(this)
        rvRecentScans.adapter = RecentScanAdapter(scans)
    }
}
