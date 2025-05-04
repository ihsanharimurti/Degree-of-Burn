package com.example.degreeofburn.data.repository

import android.content.Context
import com.example.degreeofburn.data.model.response.PatientCountResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import com.example.degreeofburn.data.remote.ApiClient
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(private val context: Context) {
    private val apiService = ApiClient.getAuthenticatedClient(context)

    suspend fun getTodayPatientCount(): Resource<PatientCountResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPatientCount("today")

                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext Resource.Success(it)
                    }
                }
                return@withContext Resource.Error("Failed to get today's patient count: ${response.message()}")
            } catch (e: Exception) {
                return@withContext Resource.Error("Network error: ${e.message}")
            }
        }
    }

    suspend fun getTotalPatientCount(): Resource<PatientCountResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPatientCount("all")

                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext Resource.Success(it)
                    }
                }
                return@withContext Resource.Error("Failed to get total patient count: ${response.message()}")
            } catch (e: Exception) {
                return@withContext Resource.Error("Network error: ${e.message}")
            }
        }
    }

    suspend fun getUserDetail(userId: String): Resource<UserDetailResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserDetail(userId)

                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext Resource.Success(it)
                    }
                }
                return@withContext Resource.Error("Failed to get user details: ${response.message()}")
            } catch (e: Exception) {
                return@withContext Resource.Error("Network error: ${e.message}")
            }
        }
    }
}