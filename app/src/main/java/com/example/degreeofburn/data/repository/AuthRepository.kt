package com.example.degreeofburn.data.repository


import com.example.degreeofburn.data.model.request.OtpRequest
import com.example.degreeofburn.data.model.response.OtpResponse
import com.example.degreeofburn.data.model.request.RegisterRequest
import com.example.degreeofburn.data.model.response.RegisterResponse
import com.example.degreeofburn.data.remote.ApiService
import com.example.degreeofburn.utils.Resource

class AuthRepository(private val apiService: ApiService) {

    suspend fun registerDoctor(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Resource<RegisterResponse> {
        return try {
            val request = RegisterRequest(
                nama = name,
                email = email,
                nomor_telepon = phone,
                password = password,
                konfirmasi_password = password
            )

            val response = apiService.registerDoctor(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Empty response body")
                }
            } else {
                Resource.Error("Registration failed: ${response.errorBody()?.string() ?: response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred during registration")
        }
    }

    suspend fun verifyOtp(otp: String, registrationToken: String): Resource<OtpResponse> {
        return try {
            val request = OtpRequest(otp, registrationToken)
            val response = apiService.verifyOtp(request)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    // Check the specific message
                    if (responseBody.message == "Pendaftaran berhasil!") {
                        Resource.Success(responseBody)
                    } else {
                        Resource.Error("Registration unsuccessful: ${responseBody.message}")
                    }
                } else {
                    Resource.Error("Empty response body")
                }
            } else {
                // Try to get detailed error from error body
                val errorMessage = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error: ${response.message()}"
                }
                Resource.Error("OTP verification failed: $errorMessage")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred during OTP verification")
        }
    }
}