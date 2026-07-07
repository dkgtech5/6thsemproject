package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScanHistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var adapter: RecentScanAdapter
    private var allScans = listOf<RecentScan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historyHeader)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btnBackHistory).setOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupFilters()
        setupNavigation()
    }

    private fun setupRecyclerView() {
        rvHistory = findViewById(R.id.rvHistory)
        allScans = listOf(
            RecentScan("google.com", "Today, 10:30 AM", true),
            RecentScan("facebook-login.xyz", "Today, 09:15 AM", false),
            RecentScan("github.com", "Yesterday, 08:40 PM", true),
            RecentScan("secure-facebook-login.info", "Yesterday, 07:10 PM", false),
            RecentScan("amazon.com", "Yesterday, 06:15 PM", true)
        )

        adapter = RecentScanAdapter(allScans)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    private fun setupFilters() {
        val filterAll = findViewById<TextView>(R.id.filterAll)
        val filterSafe = findViewById<TextView>(R.id.filterSafe)
        val filterPhishing = findViewById<TextView>(R.id.filterPhishing)

        filterAll.setOnClickListener {
            updateFilterUI(filterAll, listOf(filterSafe, filterPhishing))
            adapter = RecentScanAdapter(allScans)
            rvHistory.adapter = adapter
        }

        filterSafe.setOnClickListener {
            updateFilterUI(filterSafe, listOf(filterAll, filterPhishing))
            val filtered = allScans.filter { it.isSafe }
            adapter = RecentScanAdapter(filtered)
            rvHistory.adapter = adapter
        }

        filterPhishing.setOnClickListener {
            updateFilterUI(filterPhishing, listOf(filterAll, filterSafe))
            val filtered = allScans.filter { !it.isSafe }
            adapter = RecentScanAdapter(filtered)
            rvHistory.adapter = adapter
        }
    }

    private fun updateFilterUI(selected: TextView, unselected: List<TextView>) {
        selected.setBackgroundResource(R.drawable.btn_primary)
        selected.setTextColor(ContextCompat.getColor(this, R.color.white))
        
        for (view in unselected) {
            view.setBackgroundResource(R.drawable.btn_secondary)
            view.setTextColor(ContextCompat.getColor(this, R.color.text_grey))
        }
    }

    private fun setupNavigation() {
        // Find views in bottom nav (assuming they have IDs)
        findViewById<View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.navStats).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.fabScan).setOnClickListener {
            startActivity(Intent(this, ScanWebsiteActivity::class.java))
        }
    }
}