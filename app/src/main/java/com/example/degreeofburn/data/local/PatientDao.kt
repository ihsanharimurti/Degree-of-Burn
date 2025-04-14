package com.example.degreeofburn.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Delete
    suspend fun deletePatient(patient: PatientEntity)

    @Query("SELECT * FROM patients WHERE id = :patientId")
    suspend fun getPatientById(patientId: Long): PatientEntity?

    @Query("SELECT * FROM patients ORDER BY timestamp DESC")
    fun getAllPatients(): LiveData<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE syncedToRemote = 0")
    suspend fun getUnsyncedPatients(): List<PatientEntity>

    @Query("UPDATE patients SET syncedToRemote = 1 WHERE id = :patientId")
    suspend fun markPatientAsSynced(patientId: Long)
}
