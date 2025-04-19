package com.example.degreeofburn.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    // View binding instance
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets to handle the status bar properly
        // Set up any additional UI elements or listeners here

    }
}