package com.example.safe

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import java.util.Locale

class ScanningActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvPercentage: TextView
    private lateinit var ivChecks: List<ImageView>
    private lateinit var tvChecks: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scanning)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvAnalyzingTitle)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBarScanning)
        tvPercentage = findViewById(R.id.tvProgressPercentage)

        ivChecks = listOf(
            findViewById(R.id.ivCheckHttps),
            findViewById(R.id.ivCheckDomain),
            findViewById(R.id.ivCheckRedirects),
            findViewById(R.id.ivCheckUrlLength),
            findViewById(R.id.ivCheckContent)
        )

        tvChecks = listOf(
            findViewById(R.id.tvCheckHttps),
            findViewById(R.id.tvCheckDomain),
            findViewById(R.id.tvCheckRedirects),
            findViewById(R.id.tvCheckUrlLength),
            findViewById(R.id.tvCheckContent)
        )

        startScanningAnimation()
    }

    private fun startScanningAnimation() {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 5000 // 5 seconds for full scan
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            progressBar.progress = progress
            tvPercentage.text = String.format(Locale.getDefault(), "%d%%", progress)

            updateChecklist(progress)
        }

        animator.start()

        Handler(Looper.getMainLooper()).postDelayed({
            // For now, let's navigate to a result (we'll implement result screen next)
            // finish() 
        }, 5500)
    }

    private fun updateChecklist(progress: Int) {
        // Simple logic to check items based on progress milestones
        val milestones = listOf(20, 40, 60, 80, 100)
        for (i in milestones.indices) {
            if (progress >= milestones[i]) {
                ivChecks[i].setColorFilter(Color.parseColor("#10B981")) // Green
                tvChecks[i].setTextColor(Color.parseColor("#0F172A")) // Dark
            }
        }
    }
}