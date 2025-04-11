package com.example.degreeofburn.data.remote

import com.example.degreeofburn.data.model.LoginRequest
import com.example.degreeofburn.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}