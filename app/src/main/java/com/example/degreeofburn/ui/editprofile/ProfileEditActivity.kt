package com.example.degreeofburn.ui.editprofile

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityProfileEditBinding
import com.example.degreeofburn.utils.Resource

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var viewModel: ProfileEditViewModel

    // Password visibility flags
    private var isOldPasswordVisible = false
    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ProfileEditViewModel::class.java]

        // Initialize UI
        setupInitialState()
        setupClickListeners()
        setupPasswordVisibilityToggles()
        setupPasswordValidation()
        observeViewModel()

        // Load user data
        viewModel.getUserDetails()
    }

    private fun setupInitialState() {
        // Initialize with the new password fields disabled
        binding.inputNewPasswordEdit.isEnabled = false
        binding.inputConfirmPasswordEdit.isEnabled = false
        binding.inputNewPasswordEdit.setBackgroundResource(R.drawable.bg_edittext_greyed)
        binding.inputConfirmPasswordEdit.setBackgroundResource(R.drawable.bg_edittext_greyed)
    }

    private fun setupClickListeners() {
        // Set up back button click listener
        binding.btnBackEditProfile.setOnClickListener {
            finish()
        }

        // Set up save button click listener
        binding.btnSave.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun setupPasswordVisibilityToggles() {
        // Set up old password visibility toggle
        binding.inputOldPasswordEdit.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if touch was on the drawable (eye icon)
                val drawableRight = 2
                if (event.rawX >= (binding.inputOldPasswordEdit.right - binding.inputOldPasswordEdit.compoundDrawables[drawableRight].bounds.width())) {
                    toggleOldPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        // Set up new password visibility toggle
        binding.inputNewPasswordEdit.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if touch was on the drawable (eye icon)
                val drawableRight = 2
                if (event.rawX >= (binding.inputNewPasswordEdit.right - binding.inputNewPasswordEdit.compoundDrawables[drawableRight].bounds.width())) {
                    toggleNewPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        // Set up confirm password visibility toggle
        binding.inputConfirmPasswordEdit.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if touch was on the drawable (eye icon)
                val drawableRight = 2
                if (event.rawX >= (binding.inputConfirmPasswordEdit.right - binding.inputConfirmPasswordEdit.compoundDrawables[drawableRight].bounds.width())) {
                    toggleConfirmPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun setupPasswordValidation() {
        // Monitor old password changes to enable/disable new password field
        binding.inputOldPasswordEdit.addTextChangedListener {
            val oldPassword = binding.inputOldPasswordEdit.text.toString()
            if (oldPassword.isNotEmpty()) {
                viewModel.validateOldPassword(oldPassword)
            } else {
                disableNewPasswordField()
            }
        }
    }

    private fun observeViewModel() {
        // Observe user details
        viewModel.userDetailState.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    val userData = result.data
                    userData?.let {
                        binding.inputNameEdit.setText(it.nama)
                        binding.inputNoHPEdit.setText(it.nomor_telepon)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading if needed
                }
            }
        }

        // Observe profile update result
        viewModel.updateProfileState.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    // If no password change, finish activity
                    if (binding.inputOldPasswordEdit.text.toString().isEmpty()) {
                        finish()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Error updating profile: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading if needed
                }
            }
        }

        // Observe password change result
        viewModel.changePasswordState.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Error changing password: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading if needed
                }
            }
        }

        // Observe old password validation
        viewModel.isOldPasswordCorrect.observe(this) { isCorrect ->
            if (isCorrect) {
                enableNewPasswordField()
            } else {
                disableNewPasswordField()
            }
        }

        // Observe password match validation
        viewModel.isPasswordsMatch.observe(this) { isMatch ->
            if (!isMatch) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleOldPasswordVisibility() {
        isOldPasswordVisible = !isOldPasswordVisible

        // Change transformation method to show/hide password
        if (isOldPasswordVisible) {
            binding.inputOldPasswordEdit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.inputOldPasswordEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
        } else {
            binding.inputOldPasswordEdit.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.inputOldPasswordEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
        }

        // Move cursor to the end of text
        binding.inputOldPasswordEdit.setSelection(binding.inputOldPasswordEdit.text.length)
    }

    private fun toggleNewPasswordVisibility() {
        isNewPasswordVisible = !isNewPasswordVisible

        // Change transformation method to show/hide password
        if (isNewPasswordVisible) {
            binding.inputNewPasswordEdit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.inputNewPasswordEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
        } else {
            binding.inputNewPasswordEdit.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.inputNewPasswordEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
        }

        // Move cursor to the end of text
        binding.inputNewPasswordEdit.setSelection(binding.inputNewPasswordEdit.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible

        // Change transformation method to show/hide password
        if (isConfirmPasswordVisible) {
            binding.inputConfirmPasswordEdit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.inputConfirmPasswordEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
        } else {
            binding.inputConfirmPasswordEdit.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.inputConfirmPasswordEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
        }

        // Move cursor to the end of text
        binding.inputConfirmPasswordEdit.setSelection(binding.inputConfirmPasswordEdit.text.length)
    }

    private fun enableNewPasswordField() {
        binding.inputNewPasswordEdit.isEnabled = true
        binding.inputNewPasswordEdit.setBackgroundResource(R.drawable.bg_edittext)
        binding.inputConfirmPasswordEdit.isEnabled = true
        binding.inputConfirmPasswordEdit.setBackgroundResource(R.drawable.bg_edittext)
    }

    private fun disableNewPasswordField() {
        binding.inputNewPasswordEdit.isEnabled = false
        binding.inputNewPasswordEdit.setBackgroundResource(R.drawable.bg_edittext_greyed)
        binding.inputNewPasswordEdit.text.clear()
        binding.inputConfirmPasswordEdit.isEnabled = false
        binding.inputConfirmPasswordEdit.setBackgroundResource(R.drawable.bg_edittext_greyed)
        binding.inputConfirmPasswordEdit.text.clear()
    }

    private fun saveProfileChanges() {
        // Get values from fields
        val name = binding.inputNameEdit.text.toString()
        val phone = binding.inputNoHPEdit.text.toString()
        val oldPassword = binding.inputOldPasswordEdit.text.toString()
        val newPassword = binding.inputNewPasswordEdit.text.toString()
        val confirmPassword = binding.inputConfirmPasswordEdit.text.toString()

        // Validate input
        if (!viewModel.validateForm(name, phone)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Update profile info
        viewModel.updateUserProfile(name, phone)

        // Check if user wants to change password
        if (oldPassword.isNotEmpty()) {
            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
                return
            }

            // Change password
            viewModel.changePassword(oldPassword, newPassword, confirmPassword)
        }
    }
}