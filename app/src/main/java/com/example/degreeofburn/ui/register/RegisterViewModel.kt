package com.example.degreeofburn.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.repository.AuthRepository
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    private val _otpState = MutableLiveData<OtpState>()
    val otpState: LiveData<OtpState> = _otpState

    // Store the registration token for OTP verification
    private var registrationToken: String? = null

    fun registerDoctor(name: String, email: String, phone: String, password: String, confirmPassword: String) {
        if (!validateRegistrationInput(name, email, phone, password, confirmPassword)) {
            return
        }

        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch {
            when (val result = repository.registerDoctor(name, email, phone, password)) {
                is Resource.Success -> {
                    result.data?.let { response ->
                        registrationToken = response.registrationToken
                        _registrationState.value = RegistrationState.Success(response.message)
                    }
                }
                is Resource.Error -> {
                    _registrationState.value = RegistrationState.Error(result.message ?: "Unknown error")
                }
                else -> { /* Handle Loading state if needed */ }
            }
        }
    }

    fun verifyOtp(otp: String) {
        if (otp.isBlank()) {
            _otpState.value = OtpState.Error("OTP cannot be empty")
            return
        }

        registrationToken?.let { token ->
            _otpState.value = OtpState.Loading

            viewModelScope.launch {
                when (val result = repository.verifyOtp(otp, token)) {
                    is Resource.Success -> {
                        result.data?.let { response ->
                            _otpState.value = OtpState.Success(response.message)
                        }
                    }
                    is Resource.Error -> {
                        _otpState.value = OtpState.Error(result.message ?: "OTP verification failed")
                    }
                    else -> { /* Handle Loading state if needed */ }
                }
            }
        } ?: run {
            _otpState.value = OtpState.Error("Registration token not available")
        }
    }

    private fun validateRegistrationInput(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            _registrationState.value = RegistrationState.Error("All fields are required")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registrationState.value = RegistrationState.Error("Please enter a valid email address")
            return false
        }

        if (password != confirmPassword) {
            _registrationState.value = RegistrationState.Error("Passwords do not match")
            return false
        }

        return true
    }


    // State classes for UI updates
    sealed class RegistrationState {
        object Loading : RegistrationState()
        data class Success(val message: String) : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    sealed class OtpState {
        object Loading : OtpState()
        data class Success(val message: String) : OtpState()
        data class Error(val message: String) : OtpState()
    }
}