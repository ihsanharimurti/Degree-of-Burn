package com.example.degreeofburn.ui

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.degreeofburn.R

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Untuk Android 11 (API 30) ke atas
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Untuk Android 10 (API 29) ke bawah
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
        // Ambil TextView dari layout
        val textView: TextView = findViewById(R.id.textTitle)

        // Terapkan LinearGradient pada teks
        val textShader = LinearGradient(
            0f, 0f, 0f, textView.textSize * 3,
            intArrayOf(
                getColor(R.color.blue_start), // Warna awal
                getColor(R.color.blue_end)   // Warna akhir
            ),
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = textShader



        // Tombol Selanjutnya
        val buttonNext: Button = findViewById(R.id.buttonNext)
        buttonNext.setOnClickListener {
            // Tampilkan BottomSheetDialogFragment untuk login
            val loginFragment = LoginFragment()
            loginFragment.show(supportFragmentManager, "com.example.degreeofburn.ui.LoginFragment")
        }
    }
}
