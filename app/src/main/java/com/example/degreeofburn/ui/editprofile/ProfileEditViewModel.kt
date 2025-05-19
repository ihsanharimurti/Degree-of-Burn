package com.example.degreeofburn.ui.editprofile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.degreeofburn.data.model.response.ChangePasswordResponse
import com.example.degreeofburn.data.model.response.UpdateUserResponse
import com.example.degreeofburn.data.model.response.UserDetailResponse
import com.example.degreeofburn.data.repository.UserRepository
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileEditViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)
    private val sessionManager = SessionManager(application)

    // Store original values for comparison
    private var originalName = ""
    private var originalPhoneNumber = ""

    // LiveData for UI states
    private val _userDetailState = MutableLiveData<Resource<UserDetailResponse>>()
    val userDetailState: LiveData<Resource<UserDetailResponse>> = _userDetailState

    private val _updateProfileState = MutableLiveData<Resource<UpdateUserResponse>>()
    val updateProfileState: LiveData<Resource<UpdateUserResponse>> = _updateProfileState

    private val _changePasswordState = MutableLiveData<Resource<ChangePasswordResponse>>()
    val changePasswordState: LiveData<Resource<ChangePasswordResponse>> = _changePasswordState

    // Form validation states
    private val _isFormValid = MutableLiveData<Boolean>()

    private val _isOldPasswordCorrect = MutableLiveData<Boolean>()
    val isOldPasswordCorrect: LiveData<Boolean> = _isOldPasswordCorrect

    private val _isPasswordsMatch = MutableLiveData<Boolean>()
    val isPasswordsMatch: LiveData<Boolean> = _isPasswordsMatch

    // State to track if any data has changed
    private val _hasDataChanged = MutableLiveData<Boolean>()

    // Fetch user details
    fun getUserDetails() {
        val userId = sessionManager.getUserId() ?: return

        viewModelScope.launch {
            _userDetailState.value = Resource.Loading()
            val result = userRepository.getUserDetails(userId)
            _userDetailState.value = result

            // Store original values when data is successfully loaded
            if (result is Resource.Success) {
                result.data?.let { userData ->
                    originalName = userData.nama ?: ""
                    originalPhoneNumber = userData.nomor_telepon ?: ""
                }
            }
        }
    }

    // Update user profile
    fun updateUserProfile(name: String, phoneNumber: String) {
        val userId = sessionManager.getUserId() ?: return

        viewModelScope.launch {
            _updateProfileState.value = Resource.Loading()
            val result = userRepository.updateUserProfile(userId, name, phoneNumber)
            _updateProfileState.value = result

            // If successful, update the session
            if (result is Resource.Success) {
                result.data?.data?.let { userData ->
                    sessionManager.saveUserName(userData.nama)
                }
            }
        }
    }

    // Change password
    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        if (newPassword != confirmPassword) {
            _isPasswordsMatch.value = false
            return
        }

        _isPasswordsMatch.value = true

        viewModelScope.launch {
            _changePasswordState.value = Resource.Loading()
            val result = userRepository.changePassword(oldPassword, newPassword)
            _changePasswordState.value = result
        }
    }

    // Validate old password against an API check or a stored password
    fun validateOldPassword(password: String) {
        // For demonstration purposes, we'll use a simple check
        // In a real application, you might want to verify this against the API
        val isValid = password.isNotEmpty()
        _isOldPasswordCorrect.value = isValid
    }

    // Validate form fields
    fun validateForm(name: String, phoneNumber: String): Boolean {
        val isValid = name.isNotEmpty() && phoneNumber.isNotEmpty()
        _isFormValid.value = isValid
        return isValid
    }

    // Check if any data has changed compared to original values
    fun checkForDataChanges(name: String, phoneNumber: String, oldPassword: String): Boolean {
        val nameChanged = name != originalName
        val phoneNumberChanged = phoneNumber != originalPhoneNumber
        val passwordChangeRequested = oldPassword.isNotEmpty()

        val hasChanges = nameChanged || phoneNumberChanged || passwordChangeRequested
        _hasDataChanged.value = hasChanges
        return hasChanges
    }
}