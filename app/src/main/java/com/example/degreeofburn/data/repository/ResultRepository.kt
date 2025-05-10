package com.example.degreeofburn.data.repository

import android.content.Context
import com.example.degreeofburn.data.model.MedicalHistoryModel
import com.example.degreeofburn.data.model.ResultModel
import com.example.degreeofburn.data.remote.ApiClient
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultRepository (private val context: Context){

    private val apiService = ApiClient.getAuthenticatedClient(context)

    suspend fun getMedicalResult(): Resource<List<ResultModel>> {
        return withContext(Dispatchers.IO) {
            try {
                // Get medical records
                val medicalRecordsResponse = apiService.getAllMedicalRecords()
                if (!medicalRecordsResponse.isSuccessful) {
                    return@withContext Resource.Error("Failed to fetch medical records: ${medicalRecordsResponse.message()}")
                }
                val medicalRecords = medicalRecordsResponse.body() ?: emptyList()

                // Get patients
                val patientsResponse = apiService.getAllPatients()
                if (!patientsResponse.isSuccessful) {
                    return@withContext Resource.Error("Failed to fetch patients: ${patientsResponse.message()}")
                }
                val patients = patientsResponse.body() ?: emptyList()

                // Get user details for officer name
                val sessionManager = SessionManager(context)
                val userId = sessionManager.getUserId() ?: ""

                // Get user details from API
                val userDetailResponse = apiService.getUserDetail(userId)
                val userDetail = if (userDetailResponse.isSuccessful) userDetailResponse.body() else null

                // Map patients by ID for quick lookup
                val patientMap = patients.associateBy { it.idPasien }

                // Combine the data
                val resultItems = medicalRecords.mapNotNull { medRecord ->
                    val patient = patientMap[medRecord.idPasien]
                    if (patient != null) {
                        ResultModel.fromApiResponse(medRecord, patient, userDetail)
                    } else {
                        null
                    }
                }

                Resource.Success(resultItems)
            } catch (e: Exception) {
                Resource.Error("Error fetching medical history: ${e.message}")
            }
        }
    }
}
