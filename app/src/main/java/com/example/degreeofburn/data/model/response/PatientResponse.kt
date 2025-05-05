package com.example.degreeofburn.data.model.response

import com.google.gson.annotations.SerializedName

data class PatientResponse(
    @SerializedName("id_pasien") val idPasien: Int,
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("usia") val usia: Int,
    @SerializedName("jenis_kelamin") val jenisKelamin: String,
    @SerializedName("tanggal_masuk") val tanggalMasuk: String,
    @SerializedName("bb") val beratBadan: String,
    @SerializedName("gol_darah") val golonganDarah: String,
    @SerializedName("tb") val tinggiBadan: Int
)