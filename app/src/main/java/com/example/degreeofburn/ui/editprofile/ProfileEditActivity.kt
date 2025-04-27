package com.example.degreeofburn.ui.editprofile

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding

    // Default correct password for demonstration
    private val correctPassword = "password123"

    // Password visibility flags
    private var isOldPasswordVisible = false
    private var isNewPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize with the new password field disabled
        binding.editText5.isEnabled = false
        binding.editText5.setBackgroundResource(R.drawable.bg_edittext_greyed)

        setupClickListeners()
        setupPasswordVisibilityToggles()
        setupPasswordValidation()
    }

    private fun setupClickListeners() {
        // Set up back button click listener
        binding.btnBackEditProfile.setOnClickListener {
            finish()
        }

        // Set up save button click listener
        binding.button4.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun setupPasswordVisibilityToggles() {
        // Set up old password visibility toggle
        binding.editText4.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if touch was on the drawable (eye icon)
                val drawableRight = 2
                if (event.rawX >= (binding.editText4.right - binding.editText4.compoundDrawables[drawableRight].bounds.width())) {
                    toggleOldPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        // Set up new password visibility toggle
        binding.editText5.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if touch was on the drawable (eye icon)
                val drawableRight = 2
                if (event.rawX >= (binding.editText5.right - binding.editText5.compoundDrawables[drawableRight].bounds.width())) {
                    toggleNewPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun setupPasswordValidation() {
        // Monitor old password changes to enable/disable new password field
        binding.editText4.addTextChangedListener {
            val oldPassword = binding.editText4.text.toString()
            if (oldPassword == correctPassword) {
                enableNewPasswordField()
            } else {
                disableNewPasswordField()
            }
        }
    }

    private fun toggleOldPasswordVisibility() {
        isOldPasswordVisible = !isOldPasswordVisible

        // Change transformation method to show/hide password
        if (isOldPasswordVisible) {
            binding.editText4.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.editText4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
        } else {
            binding.editText4.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.editText4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
        }

        // Move cursor to the end of text
        binding.editText4.setSelection(binding.editText4.text.length)
    }

    private fun toggleNewPasswordVisibility() {
        isNewPasswordVisible = !isNewPasswordVisible

        // Change transformation method to show/hide password
        if (isNewPasswordVisible) {
            binding.editText5.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.editText5.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
        } else {
            binding.editText5.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.editText5.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
        }

        // Move cursor to the end of text
        binding.editText5.setSelection(binding.editText5.text.length)
    }

    private fun enableNewPasswordField() {
        binding.editText5.isEnabled = true
        binding.editText5.setBackgroundResource(R.drawable.bg_edittext)
    }

    private fun disableNewPasswordField() {
        binding.editText5.isEnabled = false
        binding.editText5.setBackgroundResource(R.drawable.bg_edittext_greyed)
        binding.editText5.text.clear()
    }

    private fun saveProfileChanges() {
        // Get values from fields
        val name = binding.editText2.text.toString()
        val phone = binding.editText3.text.toString()
        val oldPassword = binding.editText4.text.toString()
        val newPassword = binding.editText5.text.toString()

        // Validate input
        if (name.isEmpty() || phone.isEmpty()) {
            // Show error message
            // You can implement this based on your app's design
            return
        }

        // Check if user wants to change password
        if (oldPassword.isNotEmpty()) {
            if (oldPassword != correctPassword) {
                // Show incorrect password error
                return
            }

            if (newPassword.isEmpty()) {
                // Show new password required error
                return
            }

            // Update password logic here
        }

        // Save profile changes logic here

        // Show success message and navigate back
        finish()
    }
}