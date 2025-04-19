package com.example.degreeofburn.ui.imageprev


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.degreeofburn.databinding.ActivityImageResultBinding
import com.example.degreeofburn.ui.ResultActivity
import com.example.degreeofburn.ui.camera.CameraActivity


class ImageResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageResultBinding
    private val viewModel: ImageResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Set up button click listeners
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        // Retry button - go back to camera
        binding.btnRetry.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Next button - continue to analysis
        binding.btnNext.setOnClickListener {
            viewModel.imageUri.value?.let { uri ->
                // Navigate to the next screen with the image
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra(KEY_IMAGE_URI, uri.toString())
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