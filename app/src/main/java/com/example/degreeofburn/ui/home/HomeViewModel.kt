package com.example.degreeofburn.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.response.PatientCountResponse
import com.example.degreeofburn.data.model.response.ServerConnectionResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _todayPatientCount = MutableLiveData<Resource<PatientCountResponse>>()
    val todayPatientCount: LiveData<Resource<PatientCountResponse>> = _todayPatientCount

    private val _totalPatientCount = MutableLiveData<Resource<PatientCountResponse>>()
    val totalPatientCount: LiveData<Resource<PatientCountResponse>> = _totalPatientCount

    private val _userDetail = MutableLiveData<Resource<UserDetailResponse>>()
    val userDetail: LiveData<Resource<UserDetailResponse>> = _userDetail

    private val _serverConnectionStatus = MutableLiveData<Resource<List<ServerConnectionResponse>>>()
    val serverConnectionStatus: LiveData<Resource<List<ServerConnectionResponse>>> = _serverConnectionStatus


    private fun fetchTodayPatientCount() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching today's patient count")
            _todayPatientCount.value = Resource.Loading()

            try {
                val result = repository.getTodayPatientCount()
                _todayPatientCount.value = result
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching today's patient count: ${e.message}", e)
                _todayPatientCount.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    private fun fetchTotalPatientCount() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching total patient count")
            _totalPatientCount.value = Resource.Loading()

            try {
                val result = repository.getTotalPatientCount()
                _totalPatientCount.value = result
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching total patient count: ${e.message}", e)
                _totalPatientCount.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    private fun fetchUserDetail(userId: String) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching user details for ID: $userId")
            _userDetail.value = Resource.Loading()

            if (userId.isEmpty()) {
                _userDetail.value = Resource.Error("User ID is empty")
                return@launch
            }

            try {
                val result = repository.getUserDetail(userId)
                _userDetail.value = result
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching user details: ${e.message}", e)
                _userDetail.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
    fun triggerServer() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Trying connection to Server")
            _serverConnectionStatus.value = Resource.Loading()

            try {
                val result = repository.tryServerConnection()
                _serverConnectionStatus.value = result
                Log.d("HomeViewModel", "Success connected to Server")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error connecting to server: ${e.message}", e)
                _serverConnectionStatus.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
    // Convenience function to fetch all data at once
    fun fetchAllData(userId: String) {
        fetchTodayPatientCount()
        fetchTotalPatientCount()
        if (userId.isNotEmpty()) {
            fetchUserDetail(userId)
        }
    }
}