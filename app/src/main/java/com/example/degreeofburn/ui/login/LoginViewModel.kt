package com.example.degreeofburn.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.LoginResponse
import com.example.degreeofburn.data.repository.LoginRepository
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<LoginResponse>>()
    val loginResult: LiveData<Resource<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Starting login process")
            _loginResult.value = Resource.Loading()

            if (email.isEmpty() || password.isEmpty()) {
                Log.d("LoginViewModel", "Email or password empty")
                _loginResult.value = Resource.Error("Email and password cannot be empty")
                return@launch
            }

            try {
                Log.d("LoginViewModel", "Calling repository login")
                val result = repository.login(email, password)
                Log.d("LoginViewModel", "Repository returned result: $result")
                _loginResult.value = result
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception during login: ${e.message}", e)
                _loginResult.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }
}