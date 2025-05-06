package com.example.degreeofburn.ui.camera

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.data.model.request.PatientRequest
import com.example.degreeofburn.data.model.response.PatientPostResponse
import com.example.degreeofburn.data.remote.ApiClient
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Changed to AndroidViewModel to have access to application context
class CameraViewModel(application: Application) : AndroidViewModel(application) {

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

//    fun postPatientData(patientData: PatientDTO?) {
//        patientData?.let {
//            _apiStatus.value = ApiStatus.Loading
//
//            viewModelScope.launch {
//                try {
//                    // Hard-coded Firebase UID as requested
//                    val firebaseUid = "GlEtLKQS0bhbnNBRZIdJcoytP7g2"
//
//                    // Get today's date for tanggal_masuk
//                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                    val today = dateFormat.format(Date())
//
//                    // Create the request object
//                    val patientRequest = PatientRequest(
//                        nama = it.name,
//
//                    )
//
//                    // Use the application context from AndroidViewModel
//                    val response = ApiClient.getAuthenticatedClient(getApplication()).createPatient(patientRequest)
//
//                    handleApiResponse(response)
//
//                } catch (e: Exception) {
//                    Log.e("PostPatient", "Exception: ${e.message}", e)
//                    _apiStatus.value = ApiStatus.Error("Terjadi kesalahan: ${e.message}")
//                }
//            }
//        } ?: run {
//            _apiStatus.value = ApiStatus.Error("Patient data is null")
//        }
//    }

//    private fun handleApiResponse(response: Response<PatientPostResponse>) {
//        if (response.isSuccessful) {
//            response.body()?.let {
//                Log.d("PostPatient", "Success: ${it.message}")
//                _apiStatus.value = ApiStatus.Success(it.message)
//            } ?: run {
//                _apiStatus.value = ApiStatus.Error("Response body is null")
//            }
//        } else {
//            val errorMsg = response.errorBody()?.string() ?: response.message()
//            Log.e("PostPatient", "Error: ${response.code()} - $errorMsg")
//            _apiStatus.value = ApiStatus.Error("Gagal menyimpan data pasien: ${response.code()}")
//        }
//    }

//    fun resetApiStatus() {
//        _apiStatus.value = ApiStatus.None
//    }
}