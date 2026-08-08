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
    private lateinit var dbHelper: DatabaseHelper
    private var allScans = listOf<RecentScan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_history)
        
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

        findViewById<ImageView>(R.id.btnBackHistory).setOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupFilters()
        setupNavigation()
        highlightHistory()
        
        findViewById<TextView>(R.id.btnClearHistory).setOnClickListener {
            dbHelper.clearScans()
            setupRecyclerView() // Refresh list
        }
    }

    private fun highlightHistory() {
        findViewById<ImageView>(R.id.ivNavHistory).setColorFilter(android.graphics.Color.parseColor("#3B82F6"))
        findViewById<TextView>(R.id.tvNavHistory).setTextColor(android.graphics.Color.parseColor("#3B82F6"))
    }

    private fun setupRecyclerView() {
        rvHistory = findViewById(R.id.rvHistory)
        allScans = dbHelper.getRecentScans(100) // Get more for history

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