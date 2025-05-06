package com.example.degreeofburn.data.repository

import android.content.Context
import com.example.degreeofburn.data.model.request.PatientRequest
import com.example.degreeofburn.data.model.response.PatientPostResponse
import com.example.degreeofburn.data.remote.ApiClient
import retrofit2.Response

class PatientRepository(private val context: Context) {

    private val apiService = ApiClient.getAuthenticatedClient(context)

    suspend fun createPatient(patientRequest: PatientRequest): Response<PatientPostResponse> {
        return apiService.createPatient(patientRequest)
    }
}