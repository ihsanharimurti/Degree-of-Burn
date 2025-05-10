package com.example.degreeofburn.data.model

data class ResultProcessedModel (
    val idRekamMedis: Int,
    val idPasien: Int,
    val patientName: String,
    val patientWeight: String,
    val patientHeight: Int,
    val patienAge:Int,
    val patientBlood:String,
    val officerName: String,  // This will come from UserDetailResponse
    val actionDate: String,
    val diagnosa: String,
    val gambarLuka: String,
    val luasLuka: Int,
    val derajatLuka: Int,
    val kebutuhanCairan: Int
)