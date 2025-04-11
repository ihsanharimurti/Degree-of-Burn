package com.example.degreeofburn.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Color
import android.widget.TextView

fun TextView.applyDiagonalGradient(startColor: String, endColor: String) {
    val gradient = LinearGradient(
        0f, this.height.toFloat(),
        this.width.toFloat(), 0f,
        intArrayOf(
            Color.parseColor(startColor),
            Color.parseColor(endColor)
        ),
        null,
        Shader.TileMode.CLAMP
    )
    this.paint.shader = gradient
}
