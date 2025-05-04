package com.example.degreeofburn.data.model.request

data class RegisterRequest(
    val nama: String,
    val email: String,
    val nomor_telepon: String,
    val role: String = "dokter", // Default to doctor role
    val password: String,
    val konfirmasi_password: String
)