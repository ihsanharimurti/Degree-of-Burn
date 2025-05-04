package com.example.degreeofburn.data.model.response

data class UserDetailResponse(
    val nama: String,
    val nomor_telepon : String,
    val email: String,
    val success: Boolean,
    val message: String
)