package com.example.degreeofburn.ui.imageprev


import android.annotation.SuppressLint
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
import com.example.degreeofburn.ui.ResultActivity
import com.example.degreeofburn.ui.camera.CameraActivity
import com.example.degreeofburn.ui.home.MainActivity
import java.io.File


class ImageResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageResultBinding
    private val viewModel: ImageResultViewModel by viewModels()
    private var patientData: PatientDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientData = intent.getParcelableExtra("PATIENT_DATA")

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

            val file = File(imageUri.path ?: "")
            if (file.exists()) {
                val fileSizeInKB = file.length() / 1024
                val fileSizeInMB = fileSizeInKB / 1024
                Log.d("ImageSize", "Ukuran file: $fileSizeInKB KB ($fileSizeInMB MB)")
            } else {
                Log.d("ImageSize", "File tidak ditemukan di path: ${imageUri.path}")
            }

        } else {
            Toast.makeText(this, "Tidak ada gambar yang diterima", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up button click listeners
        setupButtonListeners()
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


//    private fun setupButtonListeners() {
//        // Retry button - go back to camera
//        binding.btnRetry.setOnClickListener {
//            val intent = Intent(this, CameraActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        // Next button - continue to analysis
//        binding.btnNext.setOnClickListener {
//            viewModel.imageUri.value?.let { uri ->
//                // Navigate to the next screen with the image
//                val intent = Intent(this, ResultActivity::class.java).apply {
//                    putExtra(KEY_IMAGE_URI, uri.toString())
//                }
//                startActivity(intent)
//            } ?: run {
//                Toast.makeText(this, "Tidak dapat memproses gambar", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    // Di ImageResultActivity.kt
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

        // Next button - continue to analysis
        binding.btnNext.setOnClickListener {
            viewModel.imageUri.value?.let { uri ->
                // Dapatkan patientData untuk diteruskan ke ResultActivity
                val patientData = intent.getParcelableExtra<PatientDTO>("PATIENT_DATA")

                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra(KEY_IMAGE_URI, uri.toString())
                    putExtra("PATIENT_DATA", patientData)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Tidak dapat memproses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        const val KEY_IMAGE_URI = "key_image_uri"
    }
}