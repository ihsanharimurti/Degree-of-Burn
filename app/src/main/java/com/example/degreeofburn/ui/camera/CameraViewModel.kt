//package com.example.degreeofburn.ui.camera
//
//
//import android.net.Uri
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//
//class CameraViewModel : ViewModel() {
//
//    // Capture status to track photo capture results
//    sealed class CaptureStatus {
//        data class Success(val uri: Uri) : CaptureStatus()
//        data class Error(val message: String) : CaptureStatus()
//    }
//
//    private val _captureStatus = MutableLiveData<CaptureStatus?>()
//    val captureStatus: LiveData<CaptureStatus?> = _captureStatus
//
//    fun onPhotoCapture(uri: Uri?) {
//        uri?.let {
//            _captureStatus.value = CaptureStatus.Success(it)
//        } ?: run {
//            _captureStatus.value = CaptureStatus.Error("Failed to save image")
//        }
//    }
//
//    fun onCaptureFailed(message: String) {
//        _captureStatus.value = CaptureStatus.Error(message)
//    }
//
//    fun resetCaptureStatus() {
//        _captureStatus.value = null
//    }
//}



package com.example.degreeofburn.ui.camera

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.data.model.PatientRequest
import com.example.degreeofburn.data.model.PatientResponse
import com.example.degreeofburn.data.remote.ApiClient
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel : ViewModel() {

    // Capture status to track photo capture results
    sealed class CaptureStatus {
        data class Success(val uri: Uri) : CaptureStatus()
        data class Error(val message: String) : CaptureStatus()
    }

    // API status to track patient data posting
    sealed class ApiStatus {
        object Loading : ApiStatus()
        data class Success(val message: String) : ApiStatus()
        data class Error(val message: String) : ApiStatus()
        object None : ApiStatus()
    }

    private val _captureStatus = MutableLiveData<CaptureStatus?>()
    val captureStatus: LiveData<CaptureStatus?> = _captureStatus

    private val _apiStatus = MutableLiveData<ApiStatus>(ApiStatus.None)
    val apiStatus: LiveData<ApiStatus> = _apiStatus

    fun onPhotoCapture(uri: Uri?) {
        uri?.let {
            _captureStatus.value = CaptureStatus.Success(it)
        } ?: run {
            _captureStatus.value = CaptureStatus.Error("Failed to save image")
        }
    }

    fun onCaptureFailed(message: String) {
        _captureStatus.value = CaptureStatus.Error(message)
    }

    fun resetCaptureStatus() {
        _captureStatus.value = null
    }

    fun postPatientData(patientData: PatientDTO?) {
        patientData?.let {
            _apiStatus.value = ApiStatus.Loading

            viewModelScope.launch {
                try {
                    // Hard-coded Firebase UID as requested
                    val firebaseUid = "aBu2PZPsx2PDRhawNBROiuQUYVU2"

                    // Get today's date for tanggal_masuk
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = dateFormat.format(Date())

                    // Create the request object
                    val patientRequest = PatientRequest(
                        firebase_uid = firebaseUid,
                        nama = it.name,
                        usia = it.age,
                        jenis_kelamin = it.sex,
                        tanggal_masuk = today,
                        bb = it.weight.toDouble(),
                        gol_darah = it.bloodType,
                        tb = it.height
                    )

                    // Use the ApiClient.apiService property directly
                    val response = ApiClient.apiService.createPatient(patientRequest)

                    handleApiResponse(response)

                } catch (e: Exception) {
                    Log.e("PostPatient", "Exception: ${e.message}", e)
                    _apiStatus.value = ApiStatus.Error("Terjadi kesalahan: ${e.message}")
                }
            }
        } ?: run {
            _apiStatus.value = ApiStatus.Error("Patient data is null")
        }
    }

    private fun handleApiResponse(response: Response<PatientResponse>) {
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d("PostPatient", "Success: ${it.message}")
                _apiStatus.value = ApiStatus.Success(it.message)
            } ?: run {
                _apiStatus.value = ApiStatus.Error("Response body is null")
            }
        } else {
            val errorMsg = response.errorBody()?.string() ?: response.message()
            Log.e("PostPatient", "Error: ${response.code()} - $errorMsg")
            _apiStatus.value = ApiStatus.Error("Gagal menyimpan data pasien: ${response.code()}")
        }
    }

    fun resetApiStatus() {
        _apiStatus.value = ApiStatus.None
    }
}

// Data classes for API request and response
