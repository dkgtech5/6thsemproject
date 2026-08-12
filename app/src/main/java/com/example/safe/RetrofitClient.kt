package com.example.safe

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("predict")
    fun predictUrl(@Body request: ScanRequest): Call<ScanResponse>

    @POST("send-otp")
    fun sendOtp(@Body request: OtpRequest): Call<OtpResponse>

    @POST("verify-otp")
    fun verifyOtp(@Body request: OtpVerifyRequest): Call<OtpResponse>
}

/**
 * Singleton Retrofit client to manage API requests to the FastAPI backend.
 */
object RetrofitClient {
    // Change this to http://10.0.2.2:8000/ if using Emulator
    private const val BASE_URL = "http://192.168.254.2:8000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
