package com.example.degreeofburn.data.model

data class LoginResponse(
    val message: String,
    val token: String,
    val userId: String,
    val success: Boolean
)