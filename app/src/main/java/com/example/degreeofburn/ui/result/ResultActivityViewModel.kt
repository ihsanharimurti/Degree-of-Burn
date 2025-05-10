package com.example.degreeofburn.ui.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.ResultModel
import com.example.degreeofburn.data.repository.ResultRepository
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.launch

class ResultActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ResultRepository(application)

    private val _result = MutableLiveData<Resource<ResultModel>>()
    val result: LiveData<Resource<ResultModel>> = _result

    fun getMedicalRecordById(medicalRecordId: Int) {
        _result.value = Resource.Loading()
        viewModelScope.launch {
            try {
                // Get all medical records
                val allResults = repository.getMedicalResult()

                if (allResults is Resource.Success) {
                    // Find the specific record with the given ID
                    val specificResult = allResults.data?.find { it.idRekamMedis == medicalRecordId }

                    if (specificResult != null) {
                        _result.postValue(Resource.Success(specificResult))
                    } else {
                        _result.postValue(Resource.Error("Medical record not found"))
                    }
                } else if (allResults is Resource.Error) {
                    _result.postValue(Resource.Error(allResults.message ?: "Unknown error"))
                }
            } catch (e: Exception) {
                _result.postValue(Resource.Error("Error fetching medical record: ${e.message}"))
            }
        }
    }

    // Method to determine burn degree type in Indonesian
    fun getBurnDegreeType(degree: Int): String {
        return when (degree) {
            1 -> "TIPE I"
            2 -> "TIPE II"
            3 -> "TIPE III"
            else -> "TIDAK DIKETAHUI"
        }
    }

}