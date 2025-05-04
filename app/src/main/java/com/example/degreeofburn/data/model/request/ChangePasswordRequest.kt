package com.example.degreeofburn.data.model.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
