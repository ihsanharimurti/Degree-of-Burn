package com.example.degreeofburn.ui.landing

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityLandingBinding
import com.example.degreeofburn.ui.login.LoginFragment

class LandingActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLandingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        overridePendingTransition(0, 0)

        // Set justification mode for description text
        val desctv = binding.tvDescLanding
        val titletv= binding.tvTitleLandingReg
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            titletv.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            desctv.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        // Set fullscreen mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For Android 10 (API 29) and below
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }

        // Apply LinearGradient to the title text
        val textView: TextView = findViewById(R.id.tvTitleLandingReg)
        val textShader = LinearGradient(
            0f, 0f, 0f, textView.textSize * 3,
            intArrayOf(
                getColor(R.color.blue_start), // Start color
                getColor(R.color.blue_end)   // End color
            ),
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = textShader

        // Set click listener for the next button
        binding.btnNextLanding.setOnClickListener {
            showBottomSheet()
        }

        // Initialize UI elements with 0 alpha to prepare for animation
        setupInitialVisibility()

        // Play animations
        playAnimation()
    }

    private fun setupInitialVisibility() {
        // Set initial alpha to 0 for all animated elements
        binding.decorativeCross.alpha = 0f
        binding.decorativeStethoscope.alpha = 0f
        binding.imageDoctor.alpha = 0f
        binding.tvTitleLandingReg.alpha = 0f
        binding.horizontalLine.alpha = 0f
        binding.tvDescLanding.alpha = 0f
        binding.btnNextLanding.alpha = 0f
    }

    private fun playAnimation() {
        // Floating animation for doctor image
        ObjectAnimator.ofFloat(binding.imageDoctor, View.TRANSLATION_Y, -15f, 15f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        // Floating animation for cross
        ObjectAnimator.ofFloat(binding.decorativeCross, View.TRANSLATION_X, -10f, 10f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        // Floating animation for stethoscope
        ObjectAnimator.ofFloat(binding.decorativeStethoscope, View.TRANSLATION_X, 10f, -10f).apply {
            duration = 7000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        // Create fade-in animations for all elements
        val crossFade = ObjectAnimator.ofFloat(binding.decorativeCross, View.ALPHA, 0f, 1f).setDuration(500)
        val stethoscopeFade = ObjectAnimator.ofFloat(binding.decorativeStethoscope, View.ALPHA, 0f, 1f).setDuration(500)
        val doctorFade = ObjectAnimator.ofFloat(binding.imageDoctor, View.ALPHA, 0f, 1f).setDuration(800)
        val titleFade = ObjectAnimator.ofFloat(binding.tvTitleLandingReg, View.ALPHA, 0f, 1f).setDuration(500)
        val lineFade = ObjectAnimator.ofFloat(binding.horizontalLine, View.ALPHA, 0f, 1f).setDuration(500)
        val descFade = ObjectAnimator.ofFloat(binding.tvDescLanding, View.ALPHA, 0f, 1f).setDuration(500)
        val buttonFade = ObjectAnimator.ofFloat(binding.btnNextLanding, View.ALPHA, 0f, 1f).setDuration(500)

        // Create sequential animation set
        AnimatorSet().apply {
            playSequentially(
                crossFade,
                stethoscopeFade,
                doctorFade,
                titleFade,
                lineFade,
                descFade,
                buttonFade
            )
            startDelay = 300
        }.start()

        // Add button scale animation

        // Start button scale animation with a delay
    }

    private fun showBottomSheet() {
        val bottomSheet = LoginFragment()
        bottomSheet.show(supportFragmentManager, "LoginFragment")
    }
}