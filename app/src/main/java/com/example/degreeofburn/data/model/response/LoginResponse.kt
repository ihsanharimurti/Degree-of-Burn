package com.example.degreeofburn.data.model.response

data class LoginResponse(
    val message: String,
    val token: String,
    val uid: String,
    val success: Boolean
)