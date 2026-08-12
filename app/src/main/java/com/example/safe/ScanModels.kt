package com.example.safe

import com.google.gson.annotations.SerializedName

data class ScanRequest(
    @SerializedName("url") val url: String
)

data class ScanResponse(
    @SerializedName("url") val url: String,
    @SerializedName("status") val status: String,
    @SerializedName("risk_score") val riskScore: Double,
    @SerializedName("confidence_legitimate") val confidenceLegitimate: Double,
    @SerializedName("confidence_phishing") val confidencePhishing: Double,
    @SerializedName("security_checks") val securityChecks: SecurityChecks,
    // New fields from the updated model
    @SerializedName("prediction") val prediction: String? = null,
    @SerializedName("legitimate_probability") val legitimateProbability: Double? = null,
    @SerializedName("phishing_probability") val phishingProbability: Double? = null
)

data class SecurityChecks(
    @SerializedName("https_enabled") val httpsEnabled: Boolean,
    @SerializedName("trusted_domain") val trustedDomain: Boolean,
    @SerializedName("no_suspicious_redirect") val noSuspiciousRedirect: Boolean,
    @SerializedName("clean_url_structure") val cleanUrlStructure: Boolean
)

// OTP Related Models
data class OtpRequest(
    @SerializedName("email") val email: String
)

data class OtpVerifyRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String
)

data class OtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)
