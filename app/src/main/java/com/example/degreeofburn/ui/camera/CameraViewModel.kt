package com.example.degreeofburn.ui.camera


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    // Capture status to track photo capture results
    sealed class CaptureStatus {
        data class Success(val uri: Uri) : CaptureStatus()
        data class Error(val message: String) : CaptureStatus()
    }

    private val _captureStatus = MutableLiveData<CaptureStatus?>()
    val captureStatus: LiveData<CaptureStatus?> = _captureStatus

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