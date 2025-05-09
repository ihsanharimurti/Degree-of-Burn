package com.example.degreeofburn.data.model.request

data class RekamMedisRequest(
    val id_pasien: String,
    val tanggal: String,
    val diagnosa: String,
    val gambar_luka: String,
    val luas_luka: Int,
    val derajat_luka: Int,
    val kebutuhan_cairan: Int
)
