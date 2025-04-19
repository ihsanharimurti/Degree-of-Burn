//package com.example.degreeofburn.ui.input
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.degreeofburn.R
//import android.content.Intent
//import android.widget.ArrayAdapter
//import com.example.degreeofburn.databinding.ActivityInputBinding
//import com.example.degreeofburn.ui.camera.CameraActivity
//
//class InputActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityInputBinding
//    private val selectedBodyParts = mutableSetOf<String>()
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityInputBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupToolbar()
//        setupBodyPartButtons()
//        setupNextButton()
//
//        val bloodTypes = listOf("A", "B", "AB", "O")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bloodTypes)
//        binding.inputPatientBlood.setAdapter(adapter)
//        binding.inputPatientBlood.threshold = 1
//        binding.inputPatientBlood.isFocusableInTouchMode = true
//
//        // This forces the dropdown to show on click
//        binding.inputPatientBlood.setOnClickListener {
//            binding.inputPatientBlood.showDropDown()
//        }
//    }
//    private fun setupToolbar() {
//        setSupportActionBar(binding.myToolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        // Access the back button directly by ID
//        binding.btnBackInput.setOnClickListener {
//            onBackPressed()
//        }
//    }
//    private fun setupBodyPartButtons() {
//        val headButton = binding.btnHead
//        val chestButton = binding.btnTorso
//        val armsButton = binding.btnArm
//        val genitalButton = binding.btnGenital
//        val legsButton = binding.btnLeg
//        val palmButton = binding.btnPalm
//
//        val bodyPartButtons = listOf(headButton, chestButton, armsButton, genitalButton, legsButton, palmButton)
//
//        val buttonToBodyPart = mapOf(
//            headButton to "Kepala",
//            chestButton to "Dada-Perut/Punggung",
//            armsButton to "Lengan",
//            genitalButton to "Genital",
//            legsButton to "Kaki",
//            palmButton to "Telapak Tangan"
//        )
//
//        bodyPartButtons.forEach { button ->
//            button.setOnClickListener {
//                val bodyPart = buttonToBodyPart[button] ?: return@setOnClickListener
//
//                // Reset semua tombol
//                bodyPartButtons.forEach {
//                    it.setBackgroundResource(R.drawable.bg_rct_btnbody)
//                }
//
//                // Clear pilihan sebelumnya
//                selectedBodyParts.clear()
//
//                // Tandai yang baru dipilih
//                selectedBodyParts.add(bodyPart)
//                button.setBackgroundResource(R.drawable.bg_rct_btnbody_selected)
//            }
//        }
//    }
//
//
//    private fun setupNextButton() {
//        binding.btnInputNext.setOnClickListener {
//            if (validateInputs()) {
//                // Save all the input data
//                savePatientData()
//
//                // Navigate to Camera Activity
//                val intent = Intent(this, CameraActivity::class.java)
//                intent.putExtra("SELECTED_BODY_PARTS", selectedBodyParts.toTypedArray())
//                startActivity(intent)
//            }
//        }
//    }
//
//    private fun validateInputs(): Boolean {
//        // Validate patient name
//        if (binding.inputPatientName.text.toString().trim().isEmpty()) {
//            binding.inputPatientName.error = "Nama pasien harus diisi"
//            return false
//        }
//
//        // Validate weight
//        if (binding.inputPatientWeight.text.toString().trim().isEmpty()) {
//            binding.inputPatientWeight.error = "Berat badan harus diisi"
//            return false
//        }
//
//        // Validate height
//        if (binding.inputPatientHeight.text.toString().trim().isEmpty()) {
//            binding.inputPatientHeight.error = "Tinggi badan harus diisi"
//            return false
//        }
//
//        // Validate age
//        if (binding.inputPatientAge.text.toString().trim().isEmpty()) {
//            binding.inputPatientAge.error = "Umur harus diisi"
//            return false
//        }
//
//        // Validate blood type
//        if (binding.inputPatientBlood.text.toString().trim().isEmpty()) {
//            binding.inputPatientBlood.error = "Golongan darah harus diisi"
//            return false
//        }
//
//        // Validate at least one body part is selected
//        if (selectedBodyParts.isEmpty()) {
//            // Show some error message about selecting at least one body part
//            showToast("Pilih satu bagian tubuh yang terkena luka bakar")
//            return false
//        }
//
//        return true
//    }
//
//    private fun savePatientData() {
//        val patientData = hashMapOf(
//            "name" to binding.inputPatientName.text.toString(),
//            "weight" to binding.inputPatientWeight.text.toString(),
//            "height" to binding.inputPatientHeight.text.toString(),
//            "age" to binding.inputPatientAge.text.toString(),
//            "bloodType" to binding.inputPatientBlood.text.toString(),
//            "selectedBodyParts" to selectedBodyParts.toList()
//        )
//    }
//
//    private fun showToast(message: String) {
//        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
//    }
//}


package com.example.degreeofburn.ui.input

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.degreeofburn.R
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.data.local.PatientDatabase
import com.example.degreeofburn.data.repository.PatientRepository
import com.example.degreeofburn.databinding.ActivityInputBinding
import com.example.degreeofburn.ui.camera.CameraActivity


//class InputActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityInputBinding
//    private val selectedBodyParts = mutableSetOf<String>()
//    private lateinit var viewModel: PatientViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityInputBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Initialize ViewModel
//        setupViewModel()
//
//        setupToolbar()
//        setupBodyPartButtons()
//        setupNextButton()
//        setupObservers()
//
//        val bloodTypes = listOf("A", "B", "AB", "O")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bloodTypes)
//        binding.inputPatientBlood.setAdapter(adapter)
//        binding.inputPatientBlood.threshold = 1
//        binding.inputPatientBlood.isFocusableInTouchMode = true
//
//        // This forces the dropdown to show on click
//        binding.inputPatientBlood.setOnClickListener {
//            binding.inputPatientBlood.showDropDown()
//        }
//    }
//
//    private fun setupViewModel() {
//        val database = PatientDatabase.getDatabase(applicationContext)
//        val repository = PatientRepository(database.patientDao())
//        val factory = PatientViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, factory)[PatientViewModel::class.java]
//    }
//
//    private fun setupObservers() {
//        viewModel.patientSaved.observe(this) { patientId ->
//            // Navigate to Camera Activity with the patient ID
//            val intent = Intent(this, CameraActivity::class.java)
//            intent.putExtra("SELECTED_BODY_PARTS", selectedBodyParts.toTypedArray())
//            intent.putExtra("PATIENT_ID", patientId)
//            startActivity(intent)
//        }
//
//        viewModel.errorMessage.observe(this) { message ->
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun setupToolbar() {
//        setSupportActionBar(binding.myToolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        // Access the back button directly by ID
//        binding.btnBackInput.setOnClickListener {
//            onBackPressed()
//        }
//    }
//
//    private fun setupBodyPartButtons() {
//        val headButton = binding.btnHead
//        val chestButton = binding.btnTorso
//        val armsButton = binding.btnArm
//        val genitalButton = binding.btnGenital
//        val legsButton = binding.btnLeg
//        val palmButton = binding.btnPalm
//
//        val bodyPartButtons = listOf(headButton, chestButton, armsButton, genitalButton, legsButton, palmButton)
//
//        val buttonToBodyPart = mapOf(
//            headButton to "Kepala",
//            chestButton to "Dada-Perut/Punggung",
//            armsButton to "Lengan",
//            genitalButton to "Genital",
//            legsButton to "Kaki",
//            palmButton to "Telapak Tangan"
//        )
//
//        bodyPartButtons.forEach { button ->
//            button.setOnClickListener {
//                val bodyPart = buttonToBodyPart[button] ?: return@setOnClickListener
//
//                // Reset semua tombol
//                bodyPartButtons.forEach {
//                    it.setBackgroundResource(R.drawable.bg_rct_btnbody)
//                }
//
//                // Clear pilihan sebelumnya
//                selectedBodyParts.clear()
//
//                // Tandai yang baru dipilih
//                selectedBodyParts.add(bodyPart)
//                button.setBackgroundResource(R.drawable.bg_rct_btnbody_selected)
//            }
//        }
//    }
//
//    private fun setupNextButton() {
//        binding.btnInputNext.setOnClickListener {
//            if (validateInputs()) {
//                // Save patient data through ViewModel
//                viewModel.savePatient(
//                    name = binding.inputPatientName.text.toString(),
//                    weight = binding.inputPatientWeight.text.toString(),
//                    height = binding.inputPatientHeight.text.toString(),
//                    age = binding.inputPatientAge.text.toString(),
//                    bloodType = binding.inputPatientBlood.text.toString(),
//                    selectedBodyParts = selectedBodyParts.toList()
//                )
//            }
//        }
//    }
//
//    private fun validateInputs(): Boolean {
//        // Validate patient name
//        if (binding.inputPatientName.text.toString().trim().isEmpty()) {
//            binding.inputPatientName.error = "Nama pasien harus diisi"
//            return false
//        }
//
//        // Validate weight
//        if (binding.inputPatientWeight.text.toString().trim().isEmpty()) {
//            binding.inputPatientWeight.error = "Berat badan harus diisi"
//            return false
//        }
//
//        // Validate height
//        if (binding.inputPatientHeight.text.toString().trim().isEmpty()) {
//            binding.inputPatientHeight.error = "Tinggi badan harus diisi"
//            return false
//        }
//
//        // Validate age
//        if (binding.inputPatientAge.text.toString().trim().isEmpty()) {
//            binding.inputPatientAge.error = "Umur harus diisi"
//            return false
//        }
//
//        // Validate blood type
//        if (binding.inputPatientBlood.text.toString().trim().isEmpty()) {
//            binding.inputPatientBlood.error = "Golongan darah harus diisi"
//            return false
//        }
//
//        // Validate at least one body part is selected
//        if (selectedBodyParts.isEmpty()) {
//            showToast("Pilih satu bagian tubuh yang terkena luka bakar")
//            return false
//        }
//
//        return true
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//}





import com.example.degreeofburn.data.model.PatientDTO

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

        val bloodTypes = listOf("A", "B", "AB", "O")
        val sexTypes = listOf("Laki-laki", "Perempuan")

        val bloodAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bloodTypes)
        val sexAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sexTypes)

        binding.inputPatientBlood.setAdapter(bloodAdapter)
        binding.inputPatientSex.setAdapter(sexAdapter)

        setupDropdown(binding.inputPatientBlood)
        setupDropdown(binding.inputPatientSex)

    }

    private fun setupDropdown(autoComplete: AutoCompleteTextView) {
        autoComplete.inputType = InputType.TYPE_NULL
        autoComplete.isFocusable = false
        autoComplete.isFocusableInTouchMode = false
        autoComplete.setOnClickListener {
            autoComplete.showDropDown()
        }
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnBackInput.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupBodyPartButtons() {
        val headButton = binding.btnHead
        val chestButton = binding.btnTorso
        val armsButton = binding.btnArm
        val genitalButton = binding.btnGenital
        val legsButton = binding.btnLeg
        val palmButton = binding.btnPalm

        val bodyPartButtons = listOf(headButton, chestButton, armsButton, genitalButton, legsButton, palmButton)

        val buttonToBodyPart = mapOf(
            headButton to "Kepala",
            chestButton to "Dada-Perut/Punggung",
            armsButton to "Lengan",
            genitalButton to "Genital",
            legsButton to "Kaki",
            palmButton to "Telapak Tangan"
        )

        bodyPartButtons.forEach { button ->
            button.setOnClickListener {
                val bodyPart = buttonToBodyPart[button] ?: return@setOnClickListener

                // Reset semua tombol
                bodyPartButtons.forEach {
                    it.setBackgroundResource(R.drawable.bg_rct_btnbody)
                }

                // Clear pilihan sebelumnya
                selectedBodyParts.clear()

                // Tandai yang baru dipilih
                selectedBodyParts.add(bodyPart)
                button.setBackgroundResource(R.drawable.bg_rct_btnbody_selected)
            }
        }
    }

    private fun setupNextButton() {
        binding.btnInputNext.setOnClickListener {
            if (validateInputs()) {
                // Create DTO to pass to next activity
                val patientDTO = PatientDTO(
                    name = binding.inputPatientName.text.toString(),
                    weight = binding.inputPatientWeight.text.toString(),
                    height = binding.inputPatientHeight.text.toString(),
                    age = binding.inputPatientAge.text.toString(),
                    sex = binding.inputPatientSex.text.toString(),
                    bloodType = binding.inputPatientBlood.text.toString(),
                    selectedBodyParts = ArrayList(selectedBodyParts)
                )

                // Navigate to Camera Activity with patient data
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra("PATIENT_DATA", patientDTO)
                startActivity(intent)

                Log.d("TempPatientData", patientDTO.toString())

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
            showToast("Pilih satu bagian tubuh yang terkena luka bakar")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}