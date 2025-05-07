package com.example.degreeofburn.ui.camera

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.databinding.ActivityCameraBinding
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.imageprev.ImageResultActivity
import java.io.File
import java.io.FileOutputStream
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
    private var patientData: PatientDTO? = null

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

        // Create transparent hole in overlay
        setupTransparentHole()

        patientData = intent.getParcelableExtra("PATIENT_DATA")

        // Observe viewModel states
        observeViewModel()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin kembali ke menu utama? Semua data yang telah diisi akan hilang.")
            .setPositiveButton("Ya") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setupTransparentHole() {
        // Add a layout change listener to create the transparent hole once layout is complete
        binding.previewOverlay.addOnLayoutChangeListener { view, left, top, right, bottom,
                                                           oldLeft, oldTop, oldRight, oldBottom ->
            binding.previewOverlay.post {
                // Create a custom drawable that creates a hole for the center square
                val transparentHoleDrawable = TransparentHoleDrawable(
                    binding.previewOverlay.width,
                    binding.previewOverlay.height,
                    binding.centerPreviewContainer
                )
                binding.previewOverlay.background = transparentHoleDrawable
            }
        }
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
                else -> {}
            }
        }
    }

    private fun navigateToImageResult(imageUri: Uri) {
        patientData?.imageUri = imageUri.toString()

        val intent = Intent(this, ImageResultActivity::class.java).apply {
            putExtra(KEY_IMAGE_URI, imageUri.toString())
            putExtra("PATIENT_DATA", patientData)
        }
        startActivity(intent)
        Log.d("TempPatientData", patientData.toString())
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Wait until layout is stable before capturing
        binding.root.post {
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
                        // We need to crop the taken photo to match the center square preview
                        cropCapturedImage(photoFile)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        viewModel.onCaptureFailed(exception.message ?: "Unknown error")
                    }
                }
            )
        }
    }

    private fun cropCapturedImage(photoFile: File) {
        try {
            // Load the full image
            val fullBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

            // Get the orientation from the camera
            val rotation = when (lensFacing) {
                CameraSelector.LENS_FACING_BACK -> 90
                CameraSelector.LENS_FACING_FRONT -> -90
                else -> 0
            }

            // Create a matrix for the rotation
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())

            // Apply rotation to the full bitmap
            val rotatedBitmap = Bitmap.createBitmap(
                fullBitmap,
                0,
                0,
                fullBitmap.width,
                fullBitmap.height,
                matrix,
                true
            )

            // Get the preview hole and preview container positions and dimensions
            val previewHoleBounds = Rect()
            binding.centerPreviewContainer.getGlobalVisibleRect(previewHoleBounds)

            val previewContainerBounds = Rect()
            binding.fullScreenPreview.getGlobalVisibleRect(previewContainerBounds)

            // Calculate the center point of the preview hole
            val centerX = previewHoleBounds.centerX() - previewContainerBounds.left
            val centerY = previewHoleBounds.centerY() - previewContainerBounds.top

            // Calculate the ratio of the preview hole to the preview container
            val relativeX = centerX.toFloat() / previewContainerBounds.width()
            val relativeY = centerY.toFloat() / previewContainerBounds.height()

            // Calculate zoom factor - adjust this value to increase/decrease zoom level
            val zoomFactor = 0.4f // Lower value = more zoom

            // Calculate the crop width and height with zoom applied
            val cropWidth = (rotatedBitmap.width * zoomFactor).toInt()
            val cropHeight = (rotatedBitmap.height * zoomFactor).toInt()

            // Calculate the top-left corner of the crop area, centered around the relative position
            val cropLeft = (rotatedBitmap.width * relativeX - cropWidth / 2).toInt()
            val cropTop = (rotatedBitmap.height * relativeY - cropHeight / 2).toInt()

            // Make sure the crop bounds are within the image dimensions
            val safeLeft = cropLeft.coerceIn(0, rotatedBitmap.width - cropWidth)
            val safeTop = cropTop.coerceIn(0, rotatedBitmap.height - cropHeight)

            // Log the values for debugging
            Log.d("CameraActivity", "Original bitmap: ${fullBitmap.width}x${fullBitmap.height}")
            Log.d("CameraActivity", "Rotated bitmap: ${rotatedBitmap.width}x${rotatedBitmap.height}")
            Log.d("CameraActivity", "Preview container: ${previewContainerBounds.width()}x${previewContainerBounds.height()}")
            Log.d("CameraActivity", "Preview hole: ${previewHoleBounds.width()}x${previewHoleBounds.height()}")
            Log.d("CameraActivity", "Relative position: $relativeX,$relativeY")
            Log.d("CameraActivity", "Crop dimensions: $cropLeft,$cropTop,$cropWidth,$cropHeight")
            Log.d("CameraActivity", "Safe crop: $safeLeft,$safeTop,$cropWidth,$cropHeight")

            // Crop the bitmap
            val croppedBitmap = Bitmap.createBitmap(
                rotatedBitmap,
                safeLeft,
                safeTop,
                cropWidth,
                cropHeight
            )

            // Save the cropped bitmap back to the file
            FileOutputStream(photoFile).use { fos ->
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }

            // Clean up
            fullBitmap.recycle()
            rotatedBitmap.recycle()
            croppedBitmap.recycle()

            // Now notify the ViewModel with the URI
            viewModel.onPhotoCapture(photoFile.toUri())

        } catch (e: Exception) {
            Log.e("CameraActivity", "Error cropping image", e)
            viewModel.onCaptureFailed("Failed to crop image: ${e.message}")
        }
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Configure the preview for the full screen
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.fullScreenPreview.surfaceProvider)
                }

            // Configure the ImageCapture - we'll still use the same target resolution
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

