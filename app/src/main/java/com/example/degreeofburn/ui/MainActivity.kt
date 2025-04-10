package com.example.degreeofburn.ui

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.degreeofburn.R

//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
////            // Untuk Android 11 (API 30) ke atas
////            window.setDecorFitsSystemWindows(false)
////            window.insetsController?.let {
////                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
////                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
////            }
////        } else {
////            // Untuk Android 10 (API 29) ke bawah
////            @Suppress("DEPRECATION")
////            window.decorView.systemUiVisibility = (
////                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
////                            or View.SYSTEM_UI_FLAG_FULLSCREEN
////                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                    )
////        }
//
//
//        val textView: TextView = findViewById(R.id.add_patient_text)
////        val textShader = LinearGradient(
////            0f, textView.height.toFloat(),  // kiri bawah
////            textView.width.toFloat(), 0f,   // kanan atas
////            intArrayOf(
////                Color.parseColor("#1565C0"),
////                Color.parseColor("#82C9F9")
////            ),
////            null,
////            Shader.TileMode.CLAMP
////        )
//
//        val textShader = LinearGradient(
//            0f, 0f, 0f, textView.textSize * 6,
//            intArrayOf(
//                getColor(R.color.blue_start), // Warna awal
//                getColor(R.color.blue_end)   // Warna akhir
//            ),
//            null,
//            Shader.TileMode.CLAMP
//        )
//        textView.paint.shader = textShader
//
//
//    }
//}


import android.content.Intent
import com.example.degreeofburn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View binding instance
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set data
        setUserData()
        setPatientCounters()

        // Set click listeners
        setupClickListeners()
    }

    private fun setUserData() {
        // Set user data from shared preferences or database
        binding.userName.text = getUserName() // Implement this method based on your data source
    }

    private fun getUserName(): String {
        // Get user name from SharedPreferences or your data source
        // This is just a placeholder
        return "Siva Maharani"
    }

    private fun setPatientCounters() {
        // Set patient counters from database
        val todayCount = getTodayPatientCount() // Implement this based on your data source
        val totalCount = getTotalPatientCount() // Implement this based on your data source

        // Using string formatting to replace %s placeholders in the TextView
        // Note: You might need to adjust the above line depending on your actual view IDs
    }

    private fun getTodayPatientCount(): Int {
        // Get today's patient count from your database
        // This is just a placeholder
        return 5
    }

    private fun getTotalPatientCount(): Int {
        // Get total patient count from your database
        // This is just a placeholder
        return 27
    }

    private fun setupClickListeners() {
        // Add Patient button
        binding.addPatientButton.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        // History button in bottom menu
        binding.historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Home button in bottom menu - refresh current activity or do nothing
        binding.homeButton.setOnClickListener {
            // Already on home screen, could refresh data
            refreshData()
        }

        // Profile button in bottom menu
        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Profile icon at the top
        binding.constraintLayout.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun refreshData() {
        // Refresh patient data
        setPatientCounters()
        // Additional refresh operations as needed
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        refreshData()
    }
}
