package com.example.degreeofburn.ui.inputmedreports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.response.UserDetailResponse
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager
import kotlinx.coroutines.launch

class InputMedRepViewModel (
    application: Application,
    private val repository: HomeRepository
    ) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _userDetail = MutableLiveData<Resource<UserDetailResponse>>()
    val userDetail: LiveData<Resource<UserDetailResponse>> = _userDetail

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


}