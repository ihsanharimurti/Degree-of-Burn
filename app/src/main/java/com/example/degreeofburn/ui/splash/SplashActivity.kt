package com.example.degreeofburn.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.example.degreeofburn.R
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.landing.LandingActivity
import com.example.degreeofburn.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pastikan aplikasi tema sudah digunakan sebelum setContentView
        setTheme(R.style.AppTheme)

        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        setupFullscreen()

        // Gunakan Handler dengan Looper.getMainLooper() untuk menghindari deprecation
        Handler(Looper.getMainLooper()).postDelayed({
            val sessionManager = SessionManager(this)
            val intent = if (sessionManager.isSessionValid()) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LandingActivity::class.java)
            }
            startActivity(intent)

            // Tambahkan animasi fade secara manual jika perlu
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

            finish() // Tutup splash screen
        }, 3000)
    }

    private fun setupFullscreen() {
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
    }

    // Untuk memastikan animasi juga berjalan ketika pengguna menekan tombol back
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}