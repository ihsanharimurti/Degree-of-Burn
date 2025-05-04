package com.example.degreeofburn.data.remote

import com.example.degreeofburn.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding auth token for login and registration endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("/login") || path.contains("/register") || path.contains("/verify-otp")) {
            return chain.proceed(originalRequest)
        }

        // Get token from session manager
        val token = sessionManager.getAuthToken()

        // If token is available, add it to the request
        return if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}