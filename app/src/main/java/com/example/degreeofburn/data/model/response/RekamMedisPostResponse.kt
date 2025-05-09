package com.example.degreeofburn.data.model.response

import com.google.gson.annotations.SerializedName

data class RekamMedisPostResponse(
    @SerializedName("message") val message: String,
    @SerializedName("id_rekam_medis") val idRekamMedis: Int
)