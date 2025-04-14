package com.example.degreeofburn.data.repository


import androidx.lifecycle.LiveData

import com.example.degreeofburn.data.local.PatientDao
import com.example.degreeofburn.data.local.PatientEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PatientRepository(private val patientDao: PatientDao) {

    val allPatients: LiveData<List<PatientEntity>> = patientDao.getAllPatients()

    suspend fun insertPatient(patient: PatientEntity): Long {
        return withContext(Dispatchers.IO) {
            val id = patientDao.insertPatient(patient)
            // In future, we might want to attempt immediate sync here
            id
        }
    }

    suspend fun getPatientById(id: Long): PatientEntity? {
        return withContext(Dispatchers.IO) {
            patientDao.getPatientById(id)
        }
    }

    suspend fun updatePatient(patient: PatientEntity) {
        withContext(Dispatchers.IO) {
            patientDao.updatePatient(patient)
        }
    }

    suspend fun deletePatient(patient: PatientEntity) {
        withContext(Dispatchers.IO) {
            patientDao.deletePatient(patient)
        }
    }

    // Placeholder for future API sync functionality
    suspend fun syncToApi() {
        withContext(Dispatchers.IO) {
            val unsyncedPatients = patientDao.getUnsyncedPatients()

            // This is where we would make API calls in the future
            for (patient in unsyncedPatients) {
                try {
                    // Replace with actual API call when implemented
                    // val response = apiService.sendPatientData(patient)
                    // if (response.isSuccessful) {
                    patientDao.markPatientAsSynced(patient.id)
                    // }
                } catch (e: Exception) {
                    // Handle network errors
                }
            }
        }
    }
}