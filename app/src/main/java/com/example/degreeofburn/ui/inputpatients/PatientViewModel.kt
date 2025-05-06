package com.example.degreeofburn.ui.inputpatients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.request.PatientRequest
import com.example.degreeofburn.data.model.response.PatientPostResponse
import com.example.degreeofburn.data.repository.PatientRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PatientRepository(application)

    private val _createPatientResult = MutableLiveData<Result<PatientPostResponse>>()
    val createPatientResult: LiveData<Result<PatientPostResponse>> = _createPatientResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun createPatient(
        name: String,
        age: String,
        sex: String,
        weight: String,
        height: String,
        bloodType: String
    ) {
        _isLoading.value = true

        // Get current date in the required format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val weightDouble = weight.toDoubleOrNull() ?: 0.0

        val patientRequest = PatientRequest(
            nama = name,
            usia = age,
            jenis_kelamin = sex,
            tanggal_masuk = currentDate,
            bb = weightDouble,
            gol_darah = bloodType,
            tb = height
        )

        viewModelScope.launch {
            try {
                val response = repository.createPatient(patientRequest)
                if (response.isSuccessful) {
                    _createPatientResult.value = Result.success(response.body()!!)
                } else {
                    _createPatientResult.value = Result.failure(
                        Exception("Failed to create patient: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _createPatientResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}