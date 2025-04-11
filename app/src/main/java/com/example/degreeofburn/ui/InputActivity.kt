package com.example.degreeofburn.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.degreeofburn.R

//class InputActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_input)
//    }
//}


import android.content.Intent
import android.widget.Button
import com.example.degreeofburn.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputBinding
    private val selectedBodyParts = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBodyPartButtons()
        setupNextButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Access the back button directly by ID
        binding.btnBackInput.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setupBodyPartButtons() {
        // Setup individual body part buttons with click listeners
        val headButton = binding.btnHead
        val chestButton = binding.btnTorso
        val armsButton = binding.btnArm
        val genitalButton = binding.btnGenital
        val legsButton = binding.btnLeg
        val palmButton = binding.btnLeg

        // Create a list of all body part buttons for easier management
        val bodyPartButtons = listOf(headButton, chestButton, armsButton, genitalButton, legsButton, palmButton)

        // Map buttons to their respective body parts
        val buttonToBodyPart = mapOf(
            headButton to "Kepala",
            chestButton to "Dada-Perut/Punggung",
            armsButton to "Lengan",
            genitalButton to "Genital",
            legsButton to "Kaki",
            palmButton to "Telapak Tangan"
        )

        // Set up click listeners for each button
        bodyPartButtons.forEach { button ->
            button.setOnClickListener {
                val bodyPart = buttonToBodyPart[button] ?: return@setOnClickListener

                if (selectedBodyParts.contains(bodyPart)) {
                    // If already selected, deselect
                    selectedBodyParts.remove(bodyPart)
                    button.setBackgroundResource(R.drawable.bg_rct_btnbody) // Reset to default background
                } else {
                    // If not selected, select
                    selectedBodyParts.add(bodyPart)
                    button.setBackgroundResource(R.drawable.bg_rct_btnbody_selected) // Change to selected background
                }
            }
        }
    }

    private fun setupNextButton() {
        binding.btnInputNext.setOnClickListener {
            if (validateInputs()) {
                // Save all the input data
                savePatientData()

                // Navigate to Camera Activity
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra("SELECTED_BODY_PARTS", selectedBodyParts.toTypedArray())
                startActivity(intent)
            }
        }
    }

    private fun validateInputs(): Boolean {
        // Validate patient name
        if (binding.inputPatientName.text.toString().trim().isEmpty()) {
            binding.inputPatientName.error = "Nama pasien harus diisi"
            return false
        }

        // Validate weight
        if (binding.inputPatientWeight.text.toString().trim().isEmpty()) {
            binding.inputPatientWeight.error = "Berat badan harus diisi"
            return false
        }

        // Validate height
        if (binding.inputPatientHeight.text.toString().trim().isEmpty()) {
            binding.inputPatientHeight.error = "Tinggi badan harus diisi"
            return false
        }

        // Validate age
        if (binding.inputPatientAge.text.toString().trim().isEmpty()) {
            binding.inputPatientAge.error = "Umur harus diisi"
            return false
        }

        // Validate blood type
        if (binding.inputPatientBlood.text.toString().trim().isEmpty()) {
            binding.inputPatientBlood.error = "Golongan darah harus diisi"
            return false
        }

        // Validate at least one body part is selected
        if (selectedBodyParts.isEmpty()) {
            // Show some error message about selecting at least one body part
            showToast("Pilih minimal satu bagian tubuh yang terkena luka bakar")
            return false
        }

        return true
    }

    private fun savePatientData() {
        val patientData = hashMapOf(
            "name" to binding.inputPatientName.text.toString(),
            "weight" to binding.inputPatientWeight.text.toString(),
            "height" to binding.inputPatientHeight.text.toString(),
            "age" to binding.inputPatientAge.text.toString(),
            "bloodType" to binding.inputPatientBlood.text.toString(),
            "selectedBodyParts" to selectedBodyParts.toList()
        )

        // Save this data to your preferred storage method
        // Options: SharedPreferences, Database, ViewModel, etc.
        // Example with SharedPreferences:
//        val sharedPref = getSharedPreferences("PatientData", MODE_PRIVATE)
//        with(sharedPref.edit()) {
//            putString("PATIENT_NAME", patientData["name"])
//            putString("PATIENT_WEIGHT", patientData["weight"])
//            putString("PATIENT_HEIGHT", patientData["height"])
//            putString("PATIENT_AGE", patientData["age"])
//            putString("PATIENT_BLOOD_TYPE", patientData["bloodType"])
//            putStringSet("SELECTED_BODY_PARTS", selectedBodyParts)
//            apply()
//        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}