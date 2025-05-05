package com.example.degreeofburn.data.model.response

import com.google.gson.annotations.SerializedName

data class MedicalRecordResponse(
    @SerializedName("id_rekam_medis") val idRekamMedis: Int,
    @SerializedName("id_pasien") val idPasien: Int,
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("diagnosa") val diagnosa: String,
    @SerializedName("gambar_luka") val gambarLuka: String,
    @SerializedName("luas_luka") val luasLuka: Int,
    @SerializedName("derajat_luka") val derajatLuka: Int,
    @SerializedName("kebutuhan_cairan") val kebutuhanCairan: Int
)