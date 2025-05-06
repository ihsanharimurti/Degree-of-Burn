// File: app/src/main/java/com/example/degreeofburn/data/repository/ImageRepository.kt
package com.example.degreeofburn.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.provider.MediaStore
import com.example.degreeofburn.data.model.response.MLResponse
import com.example.degreeofburn.data.remote.RetrofitClient
import com.example.degreeofburn.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageRepository {
    private val apiService = RetrofitClient.mlApiService

    fun uploadImage(context: Context, imageUri: Uri): Flow<Resource<MLResponse>> = flow {
        emit(Resource.Loading())

        try {
            // Get actual file path if possible
            val actualFilePath = getFilePathFromUri(context, imageUri)
            val imageFile: File

            if (actualFilePath != null) {
                // Use the actual file if available
                imageFile = File(actualFilePath)
                Log.d("ImageRepository", "Using existing file: ${imageFile.absolutePath}, size: ${imageFile.length()}")
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

                Log.d("ImageRepository", "Created temp file: ${imageFile.absolutePath}, size: ${imageFile.length()}")
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

            Log.d("ImageRepository", "Sending file: ${imageFile.name} (${imageFile.length()} bytes)")

            // Make API call
            val response = apiService.uploadImage(imagePart)

            // Log the request and response for debugging
            Log.d("ImageRepository", "API Request URL: ${response.raw().request.url}")
            Log.d("ImageRepository", "API Response Code: ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                    Log.d("ImageRepository", "Upload successful: $it")
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Resource.Error("Error: ${response.code()} - $errorBody"))
                Log.e("ImageRepository", "Upload failed: ${response.code()} - $errorBody")
            }

            // Only delete if it's our temporary file
            if (actualFilePath == null) {
                imageFile.delete()
            }

        } catch (e: IOException) {
            emit(Resource.Error("IO Exception: ${e.message}"))
            Log.e("ImageRepository", "IO Exception during upload", e)
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
            Log.e("ImageRepository", "Exception during upload", e)
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
            Log.e("ImageRepository", "Error getting file path from URI", e)
        }
        return null
    }
}