package com.example.degreeofburn.ui.result

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.degreeofburn.data.model.ResultModel
import com.example.degreeofburn.databinding.ActivityResultBinding
import com.example.degreeofburn.ui.history.HistoryActivity
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.imageresultpreview.ImageResultPreviewActivity
import com.example.degreeofburn.ui.profile.ProfileActivity
import com.example.degreeofburn.utils.Resource

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get medical record ID from intent
        val medicalRecordId = intent.getIntExtra("ID_REKAM_MEDIS", -1)
        if (medicalRecordId == -1) {
            Toast.makeText(this, "Invalid medical record ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupObservers()
        setupClickListeners()

        // Fetch the medical record data
        viewModel.getMedicalRecordById(medicalRecordId)
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

    private fun setupObservers() {
        viewModel.result.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { populateData(it) }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Set up navigation buttons
        binding.btnHistoryMain.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Home button in bottom menu - refresh current activity
        binding.btnHomeMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Profile button in bottom menu
        binding.btnProfileMain.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Profile icon at the top
        binding.icOfficerProfileMain.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

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

    private fun populateData(result: ResultModel) {
        // Officer information
        binding.tvOfficerAccMain.text = result.officerName
        binding.tvOfficerResult.text = result.officerName
        binding.tvDateResult.text = result.actionDate

        // Patient information
        binding.tvNameResult.text = result.patientName
        binding.tvWeightResult.text = result.patientWeight
        binding.tvHeightResult.text = result.patientHeight.toString()
        binding.tvAgeResult.text = result.patienAge.toString()
        binding.tvBloodResult.text = result.patientBlood

        // Burn information
        val burnDegreeType = viewModel.getBurnDegreeType(result.derajatLuka)
        binding.tvTypeResult.text = burnDegreeType

        // Calculate burn percentage based on body area
        binding.tvPercentResult.text = "${result.luasLuka.toString()}%"

        // Fluid requirement
        binding.tvWaterResult.text = "${result.kebutuhanCairan}\nML"

        val toimageclickListener = View.OnClickListener {
            val imageUri = result.gambarLuka
            if (imageUri.isNotEmpty()) {
                try {
                    val intent = Intent(this, ImageResultPreviewActivity::class.java)
                    intent.putExtra("IMAGE_URL", imageUri)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Tidak dapat menampilkan gambar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnImagePreview.setOnClickListener(toimageclickListener)
        binding.tvGambarLuka.setOnClickListener(toimageclickListener)

        // If you have image loading functionality
        // You can implement it here using Glide or similar library
        // For example:
        // if (result.gambarLuka.isNotEmpty()) {
        //     Glide.with(this)
        //         .load(result.gambarLuka)
        //         .into(binding.imageViewBurn) // Assume you have an ImageView for the burn image
        // }
    }
}