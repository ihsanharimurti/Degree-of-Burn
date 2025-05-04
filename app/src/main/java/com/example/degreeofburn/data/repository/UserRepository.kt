package com.example.degreeofburn.data.repository

import android.content.Context
import com.example.degreeofburn.data.model.request.ChangePasswordRequest
import com.example.degreeofburn.data.model.request.UpdateUserRequest
import com.example.degreeofburn.data.model.response.ChangePasswordResponse
import com.example.degreeofburn.data.model.response.UpdateUserResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import com.example.degreeofburn.data.remote.ApiClient
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserRepository(private val context: Context) {

    private val apiService = ApiClient.getAuthenticatedClient(context)

    suspend fun getUserDetails(userId: String): Resource<UserDetailResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserDetail(userId)
                handleApiResponse(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    suspend fun updateUserProfile(userId: String, name: String, phoneNumber: String): Resource<UpdateUserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val updateRequest = UpdateUserRequest(name, phoneNumber)
                val response = apiService.updateUser(userId, updateRequest)
                handleApiResponse(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Resource<ChangePasswordResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val changePasswordRequest = ChangePasswordRequest(oldPassword, newPassword)
                val response = apiService.changePassword(changePasswordRequest)
                handleApiResponse(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun <T> handleApiResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(body)
            } else {
                Resource.Error("Empty response body")
            }
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
            Resource.Error(errorMessage)
        }
    }
}