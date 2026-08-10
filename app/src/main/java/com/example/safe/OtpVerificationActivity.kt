package com.example.safe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var email: String = ""
    private var isForgotPassword: Boolean = false
    private var newPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_verification)

        dbHelper = DatabaseHelper(this)
        email = intent.getStringExtra("EMAIL") ?: ""
        isForgotPassword = intent.getBooleanExtra("IS_FORGOT_PASSWORD", false)
        newPassword = intent.getStringExtra("NEW_PASSWORD")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etOtp = findViewById<EditText>(R.id.etOtp)
        val btnVerify = findViewById<Button>(R.id.btnVerifyOtp)
        val tvResend = findViewById<TextView>(R.id.tvResendOtp)

        btnVerify.setOnClickListener {
            val otp = etOtp.text.toString().trim()
            if (otp.length == 6) {
                verifyOtpOnServer(otp)
            } else {
                Toast.makeText(this, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
            }
        }

        tvResend.setOnClickListener {
            resendOtp()
        }
    }

    private fun verifyOtpOnServer(otp: String) {
        val request = OtpVerifyRequest(email, otp)
        RetrofitClient.apiService.verifyOtp(request).enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    if (isForgotPassword && newPassword != null) {
                        dbHelper.updatePassword(email, newPassword!!)
                        dbHelper.verifyUser(email) // Ensure user is marked verified if they weren't
                        Toast.makeText(this@OtpVerificationActivity, "Password Updated Successfully", Toast.LENGTH_SHORT).show()
                        navigateToLogin()
                    } else {
                        dbHelper.verifyUser(email)
                        Toast.makeText(this@OtpVerificationActivity, "Email Verified Successfully. Please Login.", Toast.LENGTH_LONG).show()
                        navigateToLogin()
                    }
                } else {
                    Toast.makeText(this@OtpVerificationActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                Toast.makeText(this@OtpVerificationActivity, "Connection Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun resendOtp() {
        val request = OtpRequest(email)
        RetrofitClient.apiService.sendOtp(request).enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@OtpVerificationActivity, "OTP Resent", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                Toast.makeText(this@OtpVerificationActivity, "Failed to resend OTP", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
