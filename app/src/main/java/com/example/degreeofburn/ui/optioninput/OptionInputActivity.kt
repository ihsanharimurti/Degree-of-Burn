package com.example.degreeofburn.ui.optioninput

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.degreeofburn.databinding.ActivityOptionInputBinding
import com.example.degreeofburn.ui.inputmedreports.InputMedicReportsActivity
import com.example.degreeofburn.ui.inputpatients.InputPatientsActivity


class OptionInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOptionInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout using ViewBinding
        binding = ActivityOptionInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup click listeners
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // When optionNewPatient (actually for "Pasien Lama") is clicked,
        // navigate to InputMedicReportsActivity
        binding.optionNewPatient.setOnClickListener {
            val intent = Intent(this, InputMedicReportsActivity::class.java)
            startActivity(intent)
        }

        // When optionOldPatient (actually for "Pasien Baru") is clicked,
        // navigate to InputPatientsActivity
        binding.optionOldPatient.setOnClickListener {
            val intent = Intent(this, InputPatientsActivity::class.java)
            startActivity(intent)
        }
    }
}