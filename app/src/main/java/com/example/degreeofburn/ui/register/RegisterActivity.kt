package com.example.degreeofburn.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.data.remote.ApiClient
import com.example.degreeofburn.data.repository.AuthRepository
import com.example.degreeofburn.databinding.ActivityRegisterBinding
import com.example.degreeofburn.ui.landing.LandingActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupClickListeners()
        observeViewModel()
        setupPasswordToggle()
    }

    private fun setupViewModel() {
        val repository = AuthRepository(ApiClient.apiService)
        val factory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val name = binding.inputNameReg.text.toString().trim()
            val email = binding.inputEmailReg.text.toString().trim()
            val phone = binding.inputPhoneReg.text.toString().trim()
            val password = binding.inputPasswordReg.text.toString()
            val confirmPassword = binding.inputConfirmPass.text.toString()
            val checkBox = binding.tvEULA

            viewModel.registerDoctor(name, email, phone, password, confirmPassword, checkBox)
        }
    }

    private fun observeViewModel() {
        viewModel.registrationState.observe(this) { state ->
            when (state) {
                is RegisterViewModel.RegistrationState.Loading -> {
                    showLoading(true)
                }
                is RegisterViewModel.RegistrationState.Success -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    showOtpVerificationDialog()
                }
                is RegisterViewModel.RegistrationState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.otpState.observe(this) { state ->
            when (state) {
                is RegisterViewModel.OtpState.Loading -> {
                    showLoading(true)
                }
                is RegisterViewModel.OtpState.Success -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    // Navigate to main app screen after successful verification
                    startActivity(Intent(this, LandingActivity::class.java))
                    finish()
                }
                is RegisterViewModel.OtpState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        // Set initial state (password hidden)
        binding.inputPasswordReg.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.inputPasswordReg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
        binding.inputConfirmPass.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.inputConfirmPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)

        // Set click listener dengan area yang lebih besar
        binding.inputPasswordReg.setOnTouchListener { _, event ->
            // Perluas area klik menjadi 48dp dari ujung kanan
            val touchAreaWidthInPixels = (48 * resources.displayMetrics.density).toInt()

            // Check if the touch was in the expanded touch area
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.inputPasswordReg.right - touchAreaWidthInPixels)) {
                    // Toggle password visibility
                    isPasswordVisible = !isPasswordVisible

                    if (isPasswordVisible) {
                        // Show password
                        binding.inputPasswordReg.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.inputPasswordReg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    } else {
                        // Hide password
                        binding.inputPasswordReg.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.inputPasswordReg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
                    }

                    // Maintain cursor position
                    binding.inputPasswordReg.setSelection(binding.inputPasswordReg.text.length)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        binding.inputConfirmPass.setOnTouchListener { _, event ->
            // Perluas area klik menjadi 48dp dari ujung kanan
            val touchAreaWidthInPixels = (48 * resources.displayMetrics.density).toInt()

            // Check if the touch was in the expanded touch area
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.inputConfirmPass.right - touchAreaWidthInPixels)) {
                    // Toggle password visibility
                    isPasswordVisible = !isPasswordVisible

                    if (isPasswordVisible) {
                        // Show password
                        binding.inputConfirmPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.inputConfirmPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    } else {
                        // Hide password
                        binding.inputConfirmPass.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.inputConfirmPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
                    }

                    // Maintain cursor position
                    binding.inputConfirmPass.setSelection(binding.inputConfirmPass.text.length)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }
    private fun showOtpVerificationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_otp_verification, null)
        val otpEditText = dialogView.findViewById<EditText>(R.id.etOtp)

        AlertDialog.Builder(this)
            .setTitle("OTP Verification")
            .setMessage("Please enter the OTP sent to your email")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Verify") { _, _ ->
                val otp = otpEditText.text.toString().trim()
                viewModel.verifyOtp(otp)
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.loadingOverlay.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }
}