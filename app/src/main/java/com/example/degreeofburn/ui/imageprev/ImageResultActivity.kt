package com.example.degreeofburn.ui.imageprev

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.databinding.ActivityImageResultBinding
import com.example.degreeofburn.ui.result.ResultActivity
import com.example.degreeofburn.ui.camera.CameraActivity
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.utils.Resource

class ImageResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageResultBinding
    private val viewModel: ImageResultViewModel by viewModels()
    private var patientData: PatientDTO? = null
    private lateinit var progressDialog: ProgressDialog
    private var mlProcessingComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientData = intent.getParcelableExtra("PATIENT_DATA")

        // Initialize progress dialog
        progressDialog = ProgressDialog(this).apply {
            setMessage("Memproses gambar...")
            setCancelable(false)
        }

        // Get image URI from intent
        val imageUriString = intent.getStringExtra(CameraActivity.KEY_IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            viewModel.setImageUri(imageUri)

            // Load the image into ImageView
            Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(binding.icImagePreview)
        } else {
            Toast.makeText(this, "Tidak ada gambar yang diterima", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup observers
        setupObservers()

        // Set up button click listeners
        setupButtonListeners()
    }

    private fun setupObservers() {
        // Observe ML image processing status
        viewModel.uploadStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressDialog.setMessage("Memproses gambar...")
                    progressDialog.show()
                }
                is Resource.Success -> {
                    // Don't dismiss progress dialog yet, as we'll continue with rekam medis upload
                    mlProcessingComplete = true
                    Log.d("ImageResultActivity", "ML API Response: ${resource.data}")
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error: ${resource.message}", Toast.LENGTH_LONG).show()
                    Log.e("ImageResultActivity", "Upload error: ${resource.message}")
                }
            }
        }

        // Observe updated patient data with ML result
        viewModel.patientWithMlResult.observe(this) { updatedPatient ->
            Log.d("TempPatientDataML", updatedPatient.toString())

            // Store updated patient data
            patientData = updatedPatient

            // Continue to upload rekam medis data
            viewModel.uploadRekamMedis(this, updatedPatient)
        }

        // Observe rekam medis upload status
        viewModel.rekamMedisUploadStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressDialog.setMessage("Menyimpan data rekam medis...")
                    if (!progressDialog.isShowing) {
                        progressDialog.show()
                    }
                }
                is Resource.Success -> {
                    progressDialog.dismiss()
                    Log.d("ImageResultActivity", "Rekam Medis uploaded successfully: ${resource.data}")

                    // Continue to ResultActivity with the updated patient data
                    patientData?.let { patient ->
                        val intent = Intent(this, ResultActivity::class.java).apply {
                            putExtra(KEY_IMAGE_URI, patient.imageUri)
                            putExtra("PATIENT_DATA", patient)
                            // Include ID rekam medis from response
                            resource.data?.let { putExtra("ID_REKAM_MEDIS", it.idRekamMedis) }
                        }
                        startActivity(intent)
                        finish()
                    }
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error menyimpan data: ${resource.message}", Toast.LENGTH_LONG).show()
                    Log.e("ImageResultActivity", "Rekam medis upload error: ${resource.message}")
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Create a custom dialog
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setMessage("Apakah Anda yakin ingin kembali ke menu utama? Semua data yang telah diisi akan hilang.")
            .setPositiveButton("Ya") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
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

    private fun setupButtonListeners() {
        // Retry button - go back to camera with patientData
        binding.btnRetry.setOnClickListener {
            // Mendapatkan patientData dari intent yang diterima sebelumnya
            val patientData = intent.getParcelableExtra<PatientDTO>("PATIENT_DATA")

            val intent = Intent(this, CameraActivity::class.java).apply {
                // Mengirim kembali patientData ke CameraActivity
                putExtra("PATIENT_DATA", patientData)
            }

            startActivity(intent)
            finish()
        }

        // Next button - upload image to ML API and then continue to analysis
        binding.btnNext.setOnClickListener {
            patientData?.let { patient ->
                // Start with ML image processing
                if (!mlProcessingComplete) {
                    viewModel.uploadImageAndProcessResult(this, patient)
                }
            } ?: run {
                Toast.makeText(this, "Data pasien tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val KEY_IMAGE_URI = "key_image_uri"
    }
}