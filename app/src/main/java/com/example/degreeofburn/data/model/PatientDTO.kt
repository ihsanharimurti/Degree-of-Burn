package com.example.degreeofburn.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class PatientDTO(
    val name: String,
    val patientId: Int,
    val age: Int,
    val weight: String, // Using Any to accommodate both Double and String types
    val selectedBodyParts: List<String>,
    var imageUri: String? = null, // Optional, will be set later in CameraActivity
    val classId: Int? = null // Optional, will be set from ML response
) : Parcelable