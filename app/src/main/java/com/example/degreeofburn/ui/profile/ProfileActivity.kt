package com.example.degreeofburn.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityImageResultBinding
import com.example.degreeofburn.databinding.ActivityInputBinding
import com.example.degreeofburn.databinding.ActivityProfileBinding
import com.example.degreeofburn.databinding.ActivityProfileEditBinding
import com.example.degreeofburn.ui.HistoryActivity
import com.example.degreeofburn.ui.ProfileEditActivity
import com.example.degreeofburn.ui.ResultActivity
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.landing.LandingActivity
import com.example.degreeofburn.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()


    }


    private fun setupClickListeners() {
        binding.btnBackProfile.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
        }

        // Add Patient button
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        // History button in bottom menu
        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Anda Telah Logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }


    }
}