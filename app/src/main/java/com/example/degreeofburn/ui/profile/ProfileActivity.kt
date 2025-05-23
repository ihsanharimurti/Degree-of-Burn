package com.example.degreeofburn.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.UserDataCache
import com.example.degreeofburn.databinding.ActivityProfileBinding
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.ui.editprofile.ProfileEditActivity
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.landing.LandingActivity
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupClickListeners()
        observeViewModel()

        // Load data, akan menggunakan cache jika tersedia dan valid
        viewModel.loadData()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Alih-alih perilaku default, pindah ke MainActivity
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun setupViewModel() {
        val repository = HomeRepository(this)
        val factory = ProfileViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }



    private fun observeViewModel() {
        // Observer untuk status loading
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        // Observer untuk detail pengguna
        viewModel.userDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Loading handled by isLoading observer
                }
                is Resource.Success -> {
                    resource.data?.let { userDetail ->
                        binding.tvNameProfile.text = userDetail.nama
                        binding.tvEmailProfile.text = userDetail.email
                        binding.tvNoHPProfile.text = userDetail.nomor_telepon
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observer untuk jumlah pasien
        viewModel.patientCount.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Loading handled by isLoading observer
                }
                is Resource.Success -> {
                    resource.data?.let { response ->
                        binding.tvCountPatients.text = response.total_pasien.toString()
                    }
                }
                is Resource.Error -> {
                    Log.e("ProfileActivity", "Total count error: ${resource.message}")
                    Toast.makeText(this, "Error loading total count", Toast.LENGTH_SHORT).show()
                    binding.tvCountPatients.text = "0"
                }
                else -> {}
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            // Show loading overlay and progress bar
            binding.progressBar.visibility = View.VISIBLE

            // Disable all interactive elements
            binding.btnBackProfile.isEnabled = false
            binding.btnEditProfile.isEnabled = false
            binding.btnLogout.isEnabled = false

            // Bring the overlay and progress bar to the front
            binding.loadingOverlay.bringToFront()
            binding.progressBar.bringToFront()
        } else {
            // Hide loading overlay and progress bar
            binding.progressBar.visibility = View.GONE

            // Re-enable all interactive elements
            binding.btnBackProfile.isEnabled = true
            binding.btnEditProfile.isEnabled = true
            binding.btnLogout.isEnabled = true
        }
    }

    private fun setupClickListeners() {
        binding.btnBackProfile.setOnClickListener {
            navigateToMainActivity()
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            // Flag untuk mencatat bahwa kita perlu refresh data saat kembali
            UserDataCache.setForceRefresh(true)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    // Clear cache saat logout
                    UserDataCache.clearCache()
                    sessionManager.clearSession()
                    val intent = Intent(this, LandingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Anda Telah Logout", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        // Load data dengan memeriksa apakah perlu update (dari EditProfile)
        viewModel.loadData()
    }
}