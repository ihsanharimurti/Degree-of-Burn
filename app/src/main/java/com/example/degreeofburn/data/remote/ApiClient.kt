package com.example.degreeofburn.data.remote

import android.content.Context
import com.example.degreeofburn.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://agile-scheme-424018-g8.et.r.appspot.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Create regular client without authentication
    private val basicClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Basic retrofit instance for non-authenticated calls
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(basicClient)
        .build()

    // Create API service for public endpoints (login, register)
    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // Create authenticated client with token
    fun getAuthenticatedClient(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        val authenticatedClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(sessionManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val authenticatedRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(authenticatedClient)
            .build()

        return authenticatedRetrofit.create(ApiService::class.java)
    }
}