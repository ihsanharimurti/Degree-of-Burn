package com.example.degreeofburn.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.provider.MediaStore
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.data.model.response.MLResponse
import com.example.degreeofburn.data.model.response.RekamMedisPostResponse
import com.example.degreeofburn.data.remote.ApiClient
import com.example.degreeofburn.data.remote.RetrofitClient
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageRepository {
    private val TAG = "ImageRepository"
    private val apiService = RetrofitClient.mlApiService

    fun uploadImage(context: Context, imageUri: Uri): Flow<Resource<List<MLResponse>>> = flow {
        emit(Resource.Loading())

        try {
            // Get actual file path if possible
            val actualFilePath = getFilePathFromUri(context, imageUri)
            val imageFile: File

            if (actualFilePath != null) {
                // Use the actual file if available
                imageFile = File(actualFilePath)
                Log.d(TAG, "Using existing file: ${imageFile.absolutePath}, size: ${imageFile.length()}")
            } else {
                // Create a temporary file from the input stream
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: throw IOException("Cannot open input stream for URI: $imageUri")

                // Create a more descriptive file name with proper extension
                val fileName = "burn_image_${System.currentTimeMillis()}.jpg"
                imageFile = File(context.cacheDir, fileName)

                // Copy the data to the file
                FileOutputStream(imageFile).use { outputStream ->
                    inputStream.use { input ->
                        input.copyTo(outputStream)
                    }
                }

                Log.d(TAG, "Created temp file: ${imageFile.absolutePath}, size: ${imageFile.length()}")
            }

            // Verify file exists and has content
            if (!imageFile.exists() || imageFile.length() <= 0) {
                emit(Resource.Error("Invalid image file: File doesn't exist or is empty"))
                return@flow
            }

            // Create request body
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

            // Important: Make sure the form field name matches what your API expects
            // Common names are "file", "image", "photo", etc. - using "file" as it's common
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            Log.d(TAG, "Sending file: ${imageFile.name} (${imageFile.length()} bytes)")

            // Make API call
            val response = apiService.uploadImage(imagePart)

            // Log the request and response for debugging
            Log.d(TAG, "API Request URL: ${response.raw().request.url}")
            Log.d(TAG, "API Response Code: ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                    Log.d(TAG, "Upload successful: $it")
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Resource.Error("Error: ${response.code()} - $errorBody"))
                Log.e(TAG, "Upload failed: ${response.code()} - $errorBody")
            }

            // Only delete if it's our temporary file
            if (actualFilePath == null) {
                imageFile.delete()
            }

        } catch (e: IOException) {
            emit(Resource.Error("IO Exception: ${e.message}"))
            Log.e(TAG, "IO Exception during upload", e)
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
            Log.e(TAG, "Exception during upload", e)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Attempts to get the actual file path from a content URI
     */
    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        try {
            // Try to get the actual file path from the URI
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return cursor.getString(columnIndex)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file path from URI", e)
        }
        return null
    }

    // Upload rekam medis data to API with image
    suspend fun uploadRekamMedis(context: Context, patientData: PatientDTO): Flow<Resource<RekamMedisPostResponse>> = flow {
        emit(Resource.Loading())

        Log.d(TAG, "========== STARTING REKAM MEDIS UPLOAD ==========")
        Log.d(TAG, "Patient ID: ${patientData.patientId}")
        Log.d(TAG, "Patient Name: ${patientData.name}")
        Log.d(TAG, "Patient Age: ${patientData.age}")
        Log.d(TAG, "Patient Weight: ${patientData.weight} kg")
        Log.d(TAG, "Selected Body Parts: ${patientData.selectedBodyParts.joinToString(", ")}")
        Log.d(TAG, "Class ID (Burn Degree): ${patientData.classId}")
        Log.d(TAG, "Image URI: ${patientData.imageUri}")

        try {
            // Calculate luas luka based on selected body parts
            val luasLuka = calculateLuasLuka(patientData)
            Log.d(TAG, "Calculated Luas Luka: $luasLuka")

            // Calculate kebutuhan cairan
            val kebutuhanCairan = calculateKebutuhanCairan(patientData.weight.toDouble(), luasLuka)
            Log.d(TAG, "Calculated Kebutuhan Cairan: $kebutuhanCairan")

            // Create diagnosa text
            val diagnosa = "Luka bakar derajat ${patientData.classId} di ${patientData.selectedBodyParts.joinToString(", ")}"
            Log.d(TAG, "Diagnosa: $diagnosa")

            // Format current date for tanggal
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            Log.d(TAG, "Formatted Date: $currentDate")

            // Prepare image file
            val imageUri = Uri.parse(patientData.imageUri)
            val imageFile = convertUriToFile(context, imageUri)
            Log.d(TAG, "Image File Path: ${imageFile.absolutePath}")
            Log.d(TAG, "Image File Size: ${imageFile.length()} bytes")
            Log.d(TAG, "Image File Exists: ${imageFile.exists()}")

            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("gambar_luka", imageFile.name, requestFile)
            Log.d(TAG, "Image Part Field Name: gambar_luka")
            Log.d(TAG, "Image Part File Name: ${imageFile.name}")

            // Create request bodies for all fields
            val idPasienPart = patientData.patientId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val tanggalPart = currentDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val diagnosaPart = diagnosa.toRequestBody("text/plain".toMediaTypeOrNull())
            val luasLukaPart = luasLuka.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val derajatLukaPart = patientData.classId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val kebutuhanCairanPart = kebutuhanCairan.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            Log.d(TAG, "===== FORM DATA BEING SENT TO SERVER =====")
            Log.d(TAG, "id_pasien: ${patientData.patientId}")
            Log.d(TAG, "tanggal: $currentDate")
            Log.d(TAG, "diagnosa: $diagnosa")
            Log.d(TAG, "luas_luka: $luasLuka")
            Log.d(TAG, "derajat_luka: ${patientData.classId}")
            Log.d(TAG, "kebutuhan_cairan: $kebutuhanCairan")
            Log.d(TAG, "gambar_luka: ${imageFile.name}")

            // Get authenticated API service instead of using non-authenticated ApiClient.apiService
            Log.d(TAG, "Getting authenticated API client")
            val authenticatedApiService = ApiClient.getAuthenticatedClient(context)

            // Send multipart request using authenticated API service
            Log.d(TAG, "Sending authenticated rekam medis API request")
            val response = authenticatedApiService.uploadRekamMedis(
                idPasienPart,
                tanggalPart,
                diagnosaPart,
                imagePart,
                luasLukaPart,
                derajatLukaPart,
                kebutuhanCairanPart
            )

            // Log response details
            Log.d(TAG, "API Response Body: ${response.toString()}")

            emit(Resource.Success(response))
            Log.d(TAG, "Rekam medis upload successful")
        } catch (e: HttpException) {
            val errorMsg = "Network error: ${e.message()}"
            Log.e(TAG, "HTTP Error in uploadRekamMedis: ${e.message()}", e)
            Log.e(TAG, "Response code: ${e.code()}")
            try {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error body: $errorBody")
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to read error body", ex)
            }
            emit(Resource.Error(errorMsg))
        } catch (e: IOException) {
            val errorMsg = "IO Error: ${e.message}"
            Log.e(TAG, "IO Error in uploadRekamMedis: ${e.message}", e)
            emit(Resource.Error(errorMsg))
        } catch (e: Exception) {
            val errorMsg = "Unknown error: ${e.message}"
            Log.e(TAG, "Unknown Error in uploadRekamMedis: ${e.message}", e)
            emit(Resource.Error(errorMsg))
        } finally {
            Log.d(TAG, "========== FINISHED REKAM MEDIS UPLOAD ==========")
        }
    }

    private fun calculateLuasLuka(patientData: PatientDTO): Int {
        // Get patient age to determine rule set
        val isUnderNine = patientData.age < 9
        Log.d(TAG, "Patient is under 9: $isUnderNine")

        var luasLuka = 0

        patientData.selectedBodyParts.forEach { bodyPart ->
            val bodyPartValue = when (bodyPart) {
                "Kepala" -> if (isUnderNine) 18 else 9
                "Dada-Perut/Punggung" -> 18
                "Lengan" -> 9
                "Genital" -> 18
                "Kaki" -> if (isUnderNine) 14 else 18
                "Telapak Tangan" -> 1
                else -> 0
            }
            Log.d(TAG, "Body part: $bodyPart, Value: $bodyPartValue")
            luasLuka += bodyPartValue
        }

        Log.d(TAG, "Total luas luka: $luasLuka")
        return luasLuka
    }

    private fun calculateKebutuhanCairan(weight: Double, luasLuka: Int): Int {
        val result = (4 * weight * luasLuka).toInt()
        Log.d(TAG, "Kebutuhan cairan calculation: 4 × $weight × $luasLuka = $result")
        return result
    }

    // Helper function to convert Uri to File
    private fun convertUriToFile(context: Context, uri: Uri): File {
        Log.d(TAG, "Converting URI to File: $uri")
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
        Log.d(TAG, "Created temp file: ${tempFile.absolutePath}")

        val outputStream = FileOutputStream(tempFile)
        val bytesCopied = inputStream?.copyTo(outputStream) ?: 0
        Log.d(TAG, "Copied $bytesCopied bytes from input stream to file")

        inputStream?.close()
        outputStream.close()

        return tempFile
    }
}