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
    val name: String = "",
    val patientId: Int = 0,
    val weight: String = "",
    val selectedBodyParts: ArrayList<String> = arrayListOf(),
    var imageUri: String = ""

) : Parcelable