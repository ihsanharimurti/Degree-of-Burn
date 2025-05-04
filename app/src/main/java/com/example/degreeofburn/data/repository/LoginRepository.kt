package com.example.degreeofburn.data.repository

import com.example.degreeofburn.data.model.request.LoginRequest
import com.example.degreeofburn.data.model.response.LoginResponse
import com.example.degreeofburn.data.remote.ApiClient
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository {
    private val apiService = ApiClient.apiService

    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = apiService.login(loginRequest)

                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext Resource.Success(it)
                    }
                }
                return@withContext Resource.Error("Login failed: ${response.message()}")
            } catch (e: Exception) {
                return@withContext Resource.Error("Network error: ${e.message}")
            }
        }
    }
}