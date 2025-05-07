package com.example.degreeofburn.ui.camera

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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
}