package com.example.degreeofburn.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.UserDataCache
import com.example.degreeofburn.data.model.response.PatientCountResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager
import com.example.degreeofburn.utils.SingleLiveEvent
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

    // Event untuk mengindikasikan apakah data telah diperbarui
    private val _dataUpdated = SingleLiveEvent<Boolean>()

    fun getUserDetail(forceRefresh: Boolean = false) {
        // Cek cache dulu
        if (!forceRefresh && UserDataCache.isCacheValid() && UserDataCache.getUserDetail() != null) {
            _userDetail.postValue(Resource.Success(UserDataCache.getUserDetail()!!))
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: ""
            if (userId.isNotEmpty()) {
                val result = repository.getUserDetail(userId)
                if (result is Resource.Success) {
                    result.data?.let { userDetail ->
                        // Simpan ke cache
                        UserDataCache.setUserDetail(userDetail)
                    }
                }
                _userDetail.postValue(result)
            } else {
                _userDetail.postValue(Resource.Error("User ID not found"))
            }
            _isLoading.value = false
            _dataUpdated.value = true
        }
    }

    fun getTotalPatientCount(forceRefresh: Boolean = false) {
        // Cek cache dulu
        if (!forceRefresh && UserDataCache.isCacheValid() && UserDataCache.getPatientCount() > 0) {
            _patientCount.postValue(Resource.Success(PatientCountResponse(UserDataCache.getPatientCount())))
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getTotalPatientCount()
            if (result is Resource.Success) {
                result.data?.let { response ->
                    // Simpan ke cache
                    UserDataCache.setPatientCount(response.total_pasien)
                }
            }
            _patientCount.postValue(result)
            _isLoading.value = false
        }
    }

    fun loadData(checkForceRefresh: Boolean = true) {
        val forceRefresh = if (checkForceRefresh) UserDataCache.shouldForceRefresh() else false
        getUserDetail(forceRefresh)
        getTotalPatientCount(forceRefresh)
    }

    // Method untuk memaksa refresh data dari server

}