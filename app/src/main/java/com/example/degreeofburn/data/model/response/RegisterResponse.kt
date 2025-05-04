package com.example.degreeofburn.data.model.response

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val registrationToken: String? = null
)
