package com.example.degreeofburn.ui.imageprev

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.data.model.response.MLResponse
import com.example.degreeofburn.data.model.response.RekamMedisPostResponse
import com.example.degreeofburn.data.repository.ImageRepository
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.launch

class ImageResultViewModel : ViewModel() {

    private val imageRepository = ImageRepository()

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageUri

    private val _uploadStatus = MutableLiveData<Resource<MLResponse>>()
    val uploadStatus: LiveData<Resource<MLResponse>> = _uploadStatus

    private val _patientWithMlResult = MutableLiveData<PatientDTO>()
    val patientWithMlResult: LiveData<PatientDTO> = _patientWithMlResult

    private val _rekamMedisUploadStatus = MutableLiveData<Resource<RekamMedisPostResponse>>()
    val rekamMedisUploadStatus: LiveData<Resource<RekamMedisPostResponse>> = _rekamMedisUploadStatus

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun uploadImageAndProcessResult(context: Context, patientData: PatientDTO) {
        viewModelScope.launch {
            val uri = Uri.parse(patientData.imageUri)

            imageRepository.uploadImage(context, uri).collect { result ->
                _uploadStatus.postValue(result)

                when (result) {
                    is Resource.Success -> {
                        result.data?.let { mlResponse ->
                            // Create a new PatientDTO with the ML result
                            val updatedPatient = patientData.copy(
                                classId = mlResponse.classId
                            )
                            _patientWithMlResult.postValue(updatedPatient)

                            Log.d("ImageResultViewModel", "ML processing successful: Class ID = ${mlResponse.classId}")
                            Log.d("ImageResultViewModel", "ML processing successful: Confidence = ${mlResponse.confidence}")
                        }
                    }
                    is Resource.Error -> {
                        Log.e("ImageResultViewModel", "Upload failed: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d("ImageResultViewModel", "Upload in progress...")
                    }
                }
            }
        }
    }

    fun uploadRekamMedis(context: Context, patientData: PatientDTO) {
        viewModelScope.launch {
            imageRepository.uploadRekamMedis(context, patientData).collect { result ->
                _rekamMedisUploadStatus.postValue(result)

                when (result) {
                    is Resource.Success -> {
                        Log.d("ImageResultViewModel", "Rekam medis upload successful: ${result.data}")
                    }
                    is Resource.Error -> {
                        Log.e("ImageResultViewModel", "Rekam medis upload failed: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d("ImageResultViewModel", "Rekam medis upload in progress...")
                    }
                }
            }
        }
    }
}