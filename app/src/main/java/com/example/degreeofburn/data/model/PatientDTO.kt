package com.example.degreeofburn.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
//data class PatientDTO(
//    val name: String,
//    val weight: String,
//    val height: String,
//    val age: String,
//    val sex: String,
//    val bloodType: String,
//    val selectedBodyParts: ArrayList<String>,
//    var imageUri: String = "",
//    var burnDegree: String = "",
//    var burnPercentage: Double = 0.0
//) : Parcelable



//data class PatientDTO(
//    val name: String,
//    val selectedBodyParts: ArrayList<String>,
//    var imageUri: String = "",
//) : Parcelable

//
//import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
//
//@Parcelize
data class PatientDTO(
    val name: String,
    val patientId: Int,
    val weight: String, // Using Any to accommodate both Double and String types
    val selectedBodyParts: List<String>,
    var imageUri: String? = null, // Optional, will be set later in CameraActivity
    val classId: Int? = null // Optional, will be set from ML response

) : Parcelable