package com.example.degreeofburn.data.model.response

import com.google.gson.annotations.SerializedName

data class MLResponse(
    @SerializedName("class_id") val classId: Int,
    @SerializedName("class_name") val className: String,
    @SerializedName("confidence") val confidence: Double
)