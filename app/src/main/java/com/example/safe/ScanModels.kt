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
    @SerializedName("security_checks") val securityChecks: SecurityChecks
)

data class SecurityChecks(
    @SerializedName("https_enabled") val httpsEnabled: Boolean,
    @SerializedName("trusted_domain") val trustedDomain: Boolean,
    @SerializedName("no_suspicious_redirect") val noSuspiciousRedirect: Boolean,
    @SerializedName("clean_url_structure") val cleanUrlStructure: Boolean
)
