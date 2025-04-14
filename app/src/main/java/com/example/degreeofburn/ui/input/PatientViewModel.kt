package com.example.degreeofburn.ui.input

import androidx.lifecycle.*
import com.example.degreeofburn.data.local.PatientEntity
import com.example.degreeofburn.data.repository.PatientRepository
import kotlinx.coroutines.launch

class PatientViewModel(private val repository: PatientRepository) : ViewModel() {

    val allPatients: LiveData<List<PatientEntity>> = repository.allPatients

    private val _patientSaved = MutableLiveData<Long>()
    val patientSaved: LiveData<Long> = _patientSaved

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun savePatient(
        name: String,
        weight: String,
        height: String,
        age: String,
        bloodType: String,
        selectedBodyParts: List<String>
    ) {
        viewModelScope.launch {
            try {
                val patientEntity = PatientEntity(
                    name = name,
                    weight = weight,
                    height = height,
                    age = age,
                    bloodType = bloodType,
                    selectedBodyParts = selectedBodyParts
                )

                val id = repository.insertPatient(patientEntity)
                _patientSaved.value = id
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save patient data: ${e.message}"
            }
        }
    }

    fun syncToRemote() {
        viewModelScope.launch {
            try {
                repository.syncToApi()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to sync with remote API: ${e.message}"
            }
        }
    }
}
