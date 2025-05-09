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
import com.example.degreeofburn.databinding.ActivitySplashBinding
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.landing.LandingActivity
import com.example.degreeofburn.ui.nointernet.NoInternetActivity
import com.example.degreeofburn.utils.InternetUtils
import com.example.degreeofburn.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pastikan aplikasi tema sudah digunakan sebelum setContentView
        setTheme(R.style.AppTheme)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupFullscreen()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (InternetUtils.isInternetAvailable(this)) {
                val sessionManager = SessionManager(this)
                if (sessionManager.isSessionValid()) {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, LandingActivity::class.java)
                }
            } else {
                Intent(this, NoInternetActivity::class.java)
            }
            navigateToNextScreen(intent)
        }, 3000)
    }

    private fun navigateToNextScreen(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun setupFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
