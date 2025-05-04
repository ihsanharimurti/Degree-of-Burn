package com.example.degreeofburn.data.model.request

data class PatientRequest(
    val firebase_uid: String,
    val nama: String,
    val usia: String,
    val jenis_kelamin: String,
    val tanggal_masuk: String,
    val bb: Double,
    val gol_darah: String,
    val tb: String
)
