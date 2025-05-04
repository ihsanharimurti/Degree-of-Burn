package com.example.degreeofburn.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.response.PatientCountResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _userDetail = MutableLiveData<Resource<UserDetailResponse>>()
    val userDetail: LiveData<Resource<UserDetailResponse>> = _userDetail

    private val _patientCount = MutableLiveData<Resource<PatientCountResponse>>()
    val patientCount: LiveData<Resource<PatientCountResponse>> = _patientCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUserDetail() {
        _isLoading.value = true
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: ""
            if (userId.isNotEmpty()) {
                val result = repository.getUserDetail(userId)
                _userDetail.postValue(result)
            } else {
                _userDetail.postValue(Resource.Error("User ID not found"))
            }
            _isLoading.value = false
        }
    }

    fun getTotalPatientCount() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getTotalPatientCount()
            _patientCount.postValue(result)
            _isLoading.value = false
        }
    }

    fun fetchTotalPatientCount() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching total patient count")
            _patientCount.value = Resource.Loading()

            try {
                val result = repository.getTotalPatientCount()
                _patientCount.value = result
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching total patient count: ${e.message}", e)
                _patientCount.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

}