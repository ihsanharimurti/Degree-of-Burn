package com.example.degreeofburn.data.model.request

data class OtpRequest(
    val otp: String,
    val registrationToken: String
)