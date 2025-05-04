package com.example.degreeofburn.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
        loadData()
    }

    private fun setupViewModel() {
        val repository = HomeRepository(this)
        val factory = ProfileViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun loadData() {
        viewModel.getUserDetail()
        viewModel.getTotalPatientCount()
    }

    private fun observeViewModel() {
        // Observer untuk status loading


        // Observer untuk detail pengguna
        viewModel.userDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Jangan mengubah teks yang sudah ada ke "..."
                    // Tunggu sampai data tersedia
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
                    // Jangan mengubah teks yang sudah ada ke "..."
                    // Tunggu sampai data tersedia
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

    private fun setupClickListeners() {
        binding.btnBackProfile.setOnClickListener {
            finish()
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Anda Telah Logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning from edit profile
        loadData()
    }
}