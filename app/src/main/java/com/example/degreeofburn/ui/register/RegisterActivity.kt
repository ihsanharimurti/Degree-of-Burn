package com.example.degreeofburn.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupClickListeners()
        observeViewModel()
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

            viewModel.registerDoctor(name, email, phone, password, confirmPassword)
        }
    }

    private fun observeViewModel() {
        viewModel.registrationState.observe(this) { state ->
            when (state) {
                is RegisterViewModel.RegistrationState.Loading -> {
                    // Show loading indicator
                    // You might want to disable the button or show a progress bar
                }
                is RegisterViewModel.RegistrationState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    showOtpVerificationDialog()
                }
                is RegisterViewModel.RegistrationState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.otpState.observe(this) { state ->
            when (state) {
                is RegisterViewModel.OtpState.Loading -> {
                    // Show loading for OTP verification
                }
                is RegisterViewModel.OtpState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    // Navigate to main app screen after successful verification
                    startActivity(Intent(this, LandingActivity::class.java))
                    finish()
                }
                is RegisterViewModel.OtpState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
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
        // Implement loading indicator
    }
}