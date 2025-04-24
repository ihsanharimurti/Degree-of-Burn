package com.example.degreeofburn.data.remote

import com.example.degreeofburn.data.model.LoginRequest
import com.example.degreeofburn.data.model.LoginResponse
import com.example.degreeofburn.data.model.OtpRequest
import com.example.degreeofburn.data.model.OtpResponse
import com.example.degreeofburn.data.model.PatientRequest
import com.example.degreeofburn.data.model.PatientResponse
import com.example.degreeofburn.data.model.RegisterRequest
import com.example.degreeofburn.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun registerDoctor(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<OtpResponse>

    @POST("pasien")
    suspend fun createPatient(@Body patientRequest: PatientRequest): Response<PatientResponse>
}


