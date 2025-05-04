package com.example.degreeofburn.data.model.response

data class UpdateUserResponse(
    val status: String,
    val message: String,
    val data: UserDetailResponse? = null
)
