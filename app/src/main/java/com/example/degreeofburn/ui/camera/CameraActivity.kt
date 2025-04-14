package com.example.degreeofburn.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityCameraBinding
import com.example.degreeofburn.ui.ImageResultActivity

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private val viewModel: CameraViewModel by viewModels()

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var flashEnabled = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for camera control buttons
        binding.btnCamera.setOnClickListener { takePhoto() }
        binding.btnSwitchCam.setOnClickListener { switchCamera() }
        binding.btnFlash.setOnClickListener { toggleFlash() }

        // Observe viewModel states
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.captureStatus.observe(this) { status ->
            when (status) {
                is CameraViewModel.CaptureStatus.Success -> {
                    // Navigate to your ImageResultActivity with the captured image URI
                    navigateToImageResult(status.uri)
                    // Reset status after handling
                    viewModel.resetCaptureStatus()
                }
                is CameraViewModel.CaptureStatus.Error -> {
                    Toast.makeText(this, "Photo capture failed: ${status.message}", Toast.LENGTH_SHORT).show()
                }
                null -> { /* Ignore */ }
            }
        }
    }

    private fun navigateToImageResult(imageUri: Uri) {
        val intent = Intent(this, ImageResultActivity::class.java).apply {
            putExtra(KEY_IMAGE_URI, imageUri.toString())
        }
        startActivity(intent)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create a timestamped file
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModel.onPhotoCapture(outputFileResults.savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    viewModel.onCaptureFailed(exception.message ?: "Unknown error")
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            // Configure the ImageCapture to capture square images
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(Size(1080, 1080)) // Force square image
                .build()


            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                updateFlashUI()
            } catch (e: Exception) {
                Toast.makeText(this, "Camera initialization failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera()
    }

    private fun toggleFlash() {
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                flashEnabled = !flashEnabled
                it.cameraControl.enableTorch(flashEnabled)
                updateFlashUI()
            } else {
                Toast.makeText(this, "Flash not available on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFlashUI() {
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                binding.btnFlash.isEnabled = true
                binding.btnFlash.alpha = 1f
                binding.btnFlash.setImageResource(
                    if (flashEnabled) R.drawable.ic_flash_on else R.drawable.ic_flash_off
                )
            } else {
                binding.btnFlash.isEnabled = false
                binding.btnFlash.alpha = 0.5f
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private val outputDirectory: File by lazy {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        mediaDir ?: filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val KEY_IMAGE_URI = "key_image_uri"
    }
}