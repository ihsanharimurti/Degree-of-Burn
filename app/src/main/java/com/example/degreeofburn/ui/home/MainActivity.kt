package com.example.degreeofburn.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.databinding.ActivityMainBinding
import com.example.degreeofburn.ui.history.HistoryActivity
import com.example.degreeofburn.ui.input.InputActivity
import com.example.degreeofburn.ui.optioninput.OptionInputActivity
import com.example.degreeofburn.ui.profile.ProfileActivity
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager

class MainActivity : AppCompatActivity() {

    // View binding instance
    private lateinit var binding: ActivityMainBinding

    // ViewModel
    private lateinit var viewModel: HomeViewModel

    // Session manager
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize session manager
        sessionManager = SessionManager(this)

        // Initialize ViewModel
        setupViewModel()

        // Set click listeners
        setupClickListeners()

        // Fetch data from API
        fetchData()
    }

    private fun setupViewModel() {
        val repository = HomeRepository(this)
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Observe LiveData changes
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observe today's patient count
        viewModel.todayPatientCount.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading indicator if needed
                    binding.tvTotalTodaySum.text = "..."
                }
                is Resource.Success -> {
                    result.data?.let { response ->
                        binding.tvTotalTodaySum.text = response.total_pasien.toString()
                    }
                }
                is Resource.Error -> {
                    binding.tvTotalTodaySum.text = "0"
                    Log.e("MainActivity", "Today count error: ${result.message}")
                    Toast.makeText(this, "Error loading today's count", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        // Observe total patient count
        viewModel.totalPatientCount.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading indicator if needed
                    binding.tvTotalSum.text = "..."
                }
                is Resource.Success -> {
                    result.data?.let { response ->
                        binding.tvTotalSum.text = response.total_pasien.toString()
                    }
                }
                is Resource.Error -> {
                    binding.tvTotalSum.text = "0"
                    Log.e("MainActivity", "Total count error: ${result.message}")
                    Toast.makeText(this, "Error loading total count", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        // Observe user details
        viewModel.userDetail.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading indicator if needed
                    binding.tvOfficerAccMain.text = "Loading..."
                }
                is Resource.Success -> {
                    result.data?.let { response ->
                        binding.tvOfficerAccMain.text = response.nama
                        // Optionally save the name to SessionManager for future use
                        sessionManager.saveUserName(response.nama)
                    }
                }
                is Resource.Error -> {
                    // Fallback to name from session manager if API fails
                    val userName = sessionManager.getUserName() ?: "Unknown User"
                    binding.tvOfficerAccMain.text = userName
                    Log.e("MainActivity", "User detail error: ${result.message}")
                }
                else -> {}
            }
        }
    }

    private fun fetchData() {
        // Get user ID from session
        val userId = sessionManager.getUserId()

        if (userId.isNullOrEmpty()) {
            Log.e("MainActivity", "User ID is null or empty, cannot fetch user details")
            // Set name from session manager if available
            binding.tvOfficerAccMain.text = sessionManager.getUserName() ?: "Unknown User"
        } else {
            // Fetch all data using the user ID
            viewModel.fetchAllData(userId)
        }
    }

    private fun setupClickListeners() {
        // Add Patient button
        binding.btnAddPatient.setOnClickListener {
            val intent = Intent(this, OptionInputActivity::class.java)
            startActivity(intent)
        }

        // Also make the "Click Here" text and whole right side clickable
        binding.tvClickHere.setOnClickListener {
            val intent = Intent(this, OptionInputActivity::class.java)
            startActivity(intent)
        }

        binding.kanan.setOnClickListener {
            val intent = Intent(this, OptionInputActivity::class.java)
            startActivity(intent)
        }

        // History button in bottom menu
        binding.btnHistoryMain.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Home button in bottom menu - refresh current activity
        binding.btnHomeMain.setOnClickListener {
            // Already on home screen, refresh data
            refreshData()
        }

        // Profile button in bottom menu
        binding.btnProfileMain.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Profile icon at the top
        binding.icOfficerProfileMain.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun refreshData() {
        // Show toast to indicate refresh
        Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show()

        // Fetch data again
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        fetchData()
    }
}