package com.example.safe

import android.animation.Animator
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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ScanningActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvPercentage: TextView
    private lateinit var ivChecks: List<ImageView>
    private lateinit var tvChecks: List<TextView>
    private var apiResponse: ScanResponse? = null

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

        val url = intent.getStringExtra("URL") ?: ""
        if (url.isNotEmpty()) {
            performScan(url)
        } else {
            Toast.makeText(this, "URL is missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun performScan(url: String) {
        // Start the real API call
        val request = ScanRequest(url)
        
        RetrofitClient.apiService.predictUrl(request).enqueue(object : Callback<ScanResponse> {
            override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                if (response.isSuccessful) {
                    apiResponse = response.body()
                    android.util.Log.d("ScanningActivity", "API Success: ${apiResponse?.status}")
                } else {
                    android.util.Log.e("ScanningActivity", "API Error: ${response.code()}")
                    Toast.makeText(this@ScanningActivity, "Server Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                Toast.makeText(this@ScanningActivity, "Connection Failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        // Run the 5-second animation to keep UX smooth
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 5000 
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            progressBar.progress = progress
            tvPercentage.text = String.format(Locale.getDefault(), "%d%%", progress)
            updateChecklist(progress)
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                if (apiResponse != null) {
                    navigateToResults(apiResponse!!)
                } else {
                    // If API is slow or failed, wait a bit or handle it
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (apiResponse != null) {
                            navigateToResults(apiResponse!!)
                        } else {
                            Toast.makeText(this@ScanningActivity, "Unable to get results", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }, 2000)
                }
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator.start()
    }

    private fun navigateToResults(data: ScanResponse) {
        // Save to database
        val dbHelper = DatabaseHelper(this)
        dbHelper.saveScan(data.url, data.status == "SAFE", data.riskScore)

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("URL", data.url)
        intent.putExtra("IS_SAFE", data.status == "SAFE")
        intent.putExtra("RISK_SCORE", data.riskScore.toInt())
        
        // Pass security checks
        intent.putExtra("HTTPS", data.securityChecks.httpsEnabled)
        intent.putExtra("DOMAIN", data.securityChecks.trustedDomain)
        intent.putExtra("REDIRECT", data.securityChecks.noSuspiciousRedirect)
        intent.putExtra("STRUCTURE", data.securityChecks.cleanUrlStructure)

        // Pass new model probabilities
        intent.putExtra("LEGIT_PROB", data.legitimateProbability ?: (1.0 - (data.riskScore / 100.0)))
        intent.putExtra("PHISH_PROB", data.phishingProbability ?: (data.riskScore / 100.0))
        
        startActivity(intent)
        finish()
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