package com.example.safe

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val url = intent.getStringExtra("URL") ?: "https://example.com"
        val isSafe = intent.getBooleanExtra("IS_SAFE", true)
        val riskScore = intent.getIntExtra("RISK_SCORE", if (isSafe) 5 else 94)

        setupUI(url, isSafe, riskScore)
    }

    private fun setupUI(url: String, isSafe: Boolean, riskScore: Int) {
        val ivResultIcon = findViewById<ImageView>(R.id.ivResultIcon)
        val tvResultStatus = findViewById<TextView>(R.id.tvResultStatus)
        val tvResultSubtitle = findViewById<TextView>(R.id.tvResultSubtitle)
        val tvRiskPercentage = findViewById<TextView>(R.id.tvRiskPercentage)
        val pbRisk = findViewById<ProgressBar>(R.id.pbRisk)
        val tvResultUrl = findViewById<TextView>(R.id.tvResultUrl)
        val tvDetailsHeader = findViewById<TextView>(R.id.tvDetailsHeader)
        val llChecklist = findViewById<LinearLayout>(R.id.llResultChecklist)
        val btnPrimary = findViewById<Button>(R.id.btnPrimaryAction)
        val btnSecondary = findViewById<Button>(R.id.btnSecondaryAction)

        tvResultUrl.text = url
        tvRiskPercentage.text = "$riskScore%"
        pbRisk.progress = riskScore

        if (isSafe) {
            ivResultIcon.setImageResource(R.drawable.ic_check_circle)
            ivResultIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
            tvResultStatus.text = "SAFE"
            tvResultStatus.setTextColor(Color.parseColor("#10B981"))
            tvResultSubtitle.text = "This website appears to be legitimate"
            tvRiskPercentage.setTextColor(Color.parseColor("#10B981"))
            tvDetailsHeader.text = "Security Checks"
            
            btnPrimary.text = "Visit Website"
            btnPrimary.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visit, 0, 0, 0)
            btnSecondary.text = "Scan Another"

            addChecklistItem(llChecklist, "HTTPS Enabled", true)
            addChecklistItem(llChecklist, "Trusted Domain", true)
            addChecklistItem(llChecklist, "No Suspicious Redirect", true)
            addChecklistItem(llChecklist, "Clean URL Structure", true)
        } else {
            ivResultIcon.setImageResource(R.drawable.ic_error)
            ivResultIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#EF4444"))
            tvResultStatus.text = "WARNING"
            tvResultStatus.setTextColor(Color.parseColor("#EF4444"))
            tvResultSubtitle.text = "This website is likely phishing!"
            tvRiskPercentage.setTextColor(Color.parseColor("#EF4444"))
            tvDetailsHeader.text = "Threat Reasons"
            
            btnPrimary.text = "Report Website"
            btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EF4444"))
            btnPrimary.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0)
            btnSecondary.text = "Go Back"

            addChecklistItem(llChecklist, "Suspicious Domain", false)
            addChecklistItem(llChecklist, "Abnormal URL Length", false)
            addChecklistItem(llChecklist, "Multiple Redirects Detected", false)
            addChecklistItem(llChecklist, "Unsafe Content Pattern", false)
        }

        btnSecondary.setOnClickListener {
            finish()
        }

        btnPrimary.setOnClickListener {
            if (isSafe) {
                // Visit website logic
            } else {
                val intent = Intent(this, ReportWebsiteActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
            }
        }
    }

    private fun addChecklistItem(parent: LinearLayout, text: String, isSuccess: Boolean) {
        val itemView = LayoutInflater.from(this).inflate(R.layout.item_result_check, parent, false)
        val icon = itemView.findViewById<ImageView>(R.id.ivCheckIcon)
        val tv = itemView.findViewById<TextView>(R.id.tvCheckText)

        tv.text = text
        if (isSuccess) {
            icon.setImageResource(R.drawable.ic_check_circle)
            icon.imageTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
        } else {
            icon.setImageResource(R.drawable.ic_error)
            icon.imageTintList = ColorStateList.valueOf(Color.parseColor("#EF4444"))
        }
        parent.addView(itemView)
    }
}