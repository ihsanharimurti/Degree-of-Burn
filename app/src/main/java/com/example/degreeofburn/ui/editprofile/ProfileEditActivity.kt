package com.example.degreeofburn.ui.editprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.UserDataCache
import com.example.degreeofburn.databinding.ActivityProfileEditBinding
import com.example.degreeofburn.ui.landing.LandingActivity
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
        setupTextChangeListeners()
        observeViewModel()

        // Show loading overlay
        showLoading(true)

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

    private fun setupTextChangeListeners() {
        // Add text change listeners to track changes in fields
        binding.inputNameEdit.addTextChangedListener {}
        binding.inputNoHPEdit.addTextChangedListener {}
        // Old password already has a listener for validation
    }

    private fun setupClickListeners() {
        // Set up back button click listener
        binding.btnBackEditProfile.setOnClickListener {
            finish()
        }

        // Set up save button click listener
        binding.btnSave.setOnClickListener {
            val name = binding.inputNameEdit.text.toString()
            val phone = binding.inputNoHPEdit.text.toString()
            val oldPassword = binding.inputOldPasswordEdit.text.toString()

            // Check if any data has changed
            if (!viewModel.checkForDataChanges(name, phone, oldPassword)) {
                // No changes detected
                Toast.makeText(this, "Tidak ada data yang diubah", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Changes detected - show confirmation dialog
            val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setMessage("Apakah Anda yakin ingin melakukan perubahan pada profil anda?")
                .setPositiveButton("Ya") { _, _ ->
                    saveProfileChanges()
                }
                .setNegativeButton("Batal", null)
                .create()

            // Set custom background
            dialog.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)

            // Show the dialog
            dialog.show()

            // Style the buttons
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(ContextCompat.getColor(context, R.color.blue_start))
                setTypeface(typeface, Typeface.BOLD)
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                setTextColor(ContextCompat.getColor(context, R.color.red_logout))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordVisibilityToggles() {
        // Set up old password visibility toggle
        binding.inputOldPasswordEdit.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Perluas area klik dengan menambah offset
                val drawableRight = 2
                val drawableWidth = binding.inputOldPasswordEdit.compoundDrawables[drawableRight].bounds.width()

                // Tambahkan extra padding (40dp) untuk memperbesar area klik
                val extraPadding = 40 * resources.displayMetrics.density

                if (event.rawX >= (binding.inputOldPasswordEdit.right - drawableWidth - extraPadding)) {
                    toggleOldPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        // Terapkan juga ke dua field password lainnya dengan cara yang sama
        binding.inputNewPasswordEdit.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2
                val drawableWidth = binding.inputNewPasswordEdit.compoundDrawables[drawableRight].bounds.width()
                val extraPadding = 40 * resources.displayMetrics.density

                if (event.rawX >= (binding.inputNewPasswordEdit.right - drawableWidth - extraPadding)) {
                    toggleNewPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        binding.inputConfirmPasswordEdit.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2
                val drawableWidth = binding.inputConfirmPasswordEdit.compoundDrawables[drawableRight].bounds.width()
                val extraPadding = 40 * resources.displayMetrics.density

                if (event.rawX >= (binding.inputConfirmPasswordEdit.right - drawableWidth - extraPadding)) {
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
                    showLoading(false)
                    val userData = result.data
                    userData?.let {
                        binding.inputNameEdit.setText(it.nama)
                        binding.inputNoHPEdit.setText(it.nomor_telepon)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        // Observe profile update result
        viewModel.updateProfileState.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Profil berhasil di update", Toast.LENGTH_SHORT).show()
                    // If no password change, finish activity
                    if (binding.inputOldPasswordEdit.text.toString().isEmpty()) {
                        finish()
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error updating profile: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        // Observe password change result
        viewModel.changePasswordState.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()

                    // Clear cache and logout after password change
                    UserDataCache.clearCache()
                    val intent = Intent(this, LandingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error changing password: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showLoading(true)
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

        // Show loading overlay
        showLoading(true)

        // Update profile info
        viewModel.updateUserProfile(name, phone)

        // Check if user wants to change password
        if (oldPassword.isNotEmpty()) {
            if (newPassword.isEmpty()) {
                showLoading(false)
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
                return
            }

            // Change password
            viewModel.changePassword(oldPassword, newPassword, confirmPassword)
        }
    }

    // Show or hide loading overlay
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            // Show loading overlay and progress bar
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE

            // Disable all interactive elements
            binding.btnSave.isEnabled = false
            binding.btnBackEditProfile.isEnabled = false

            // Bring the overlay and progress bar to the front
            binding.loadingOverlay.bringToFront()
            binding.progressBar.bringToFront()
        } else {
            // Hide loading overlay and progress bar
            binding.loadingOverlay.visibility = View.GONE
            binding.progressBar.visibility = View.GONE

            // Re-enable all interactive elements
            binding.btnSave.isEnabled = true
            binding.btnBackEditProfile.isEnabled = true
        }
    }
}