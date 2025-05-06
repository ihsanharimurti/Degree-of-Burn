package com.example.degreeofburn.ui.inputpatients

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityInputPatientsBinding
import com.example.degreeofburn.ui.camera.CameraActivity
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.ui.inputmedreports.InputMedicReportsActivity

class InputPatientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputPatientsBinding
    private lateinit var viewModel: PatientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[PatientViewModel::class.java]

        setupToolbar()
        setupDropdowns()
        setupNextButton()
        observeViewModel()
    }



    private fun setupToolbar() {
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnBackInputNew.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupDropdowns() {
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

    private fun setupNextButton() {
        binding.btnInputNext.setOnClickListener {
            if (validateInputs()) {
                // Submit patient data to API
                viewModel.createPatient(
                    name = binding.inputPatientName.text.toString(),
                    age = binding.inputPatientAge.text.toString(),
                    sex = binding.inputPatientSex.text.toString(),
                    weight = binding.inputPatientWeight.text.toString(),
                    height = binding.inputPatientHeight.text.toString(),
                    bloodType = binding.inputPatientBlood.text.toString()
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnInputNext.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.createPatientResult.observe(this) { result ->
            result.onSuccess { response ->
                Log.d("InputPatientsActivity", "Patient created successfully: ${response.id}")

                // Create DTO to pass to next activity
                showToast("Berhasil Membuat Pasien")
                // Navigate to Camera Activity with patient data
                val intent = Intent(this, InputMedicReportsActivity::class.java)
                startActivity(intent)
            }.onFailure { error ->
                Log.e("InputPatientsActivity", "Error creating patient", error)
                showToast("Gagal membuat pasien: ${error.message}")
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

        // Validate gender
        if (binding.inputPatientSex.text.toString().trim().isEmpty()) {
            binding.inputPatientSex.error = "Jenis kelamin harus diisi"
            return false
        }

        // Validate blood type
        if (binding.inputPatientBlood.text.toString().trim().isEmpty()) {
            binding.inputPatientBlood.error = "Golongan darah harus diisi"
            return false
        }

        return true
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Periksa apakah ada setidaknya satu data yang sudah diisi
        if (isAnyDataFilled()) {
            // Jika ada data yang diisi, tampilkan dialog konfirmasi
            val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setMessage("Apakah Anda yakin ingin kembali ke menu utama? Semua data yang telah diisi akan hilang.")
                .setPositiveButton("Ya") { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal", null)
                .create()

            // Set custom background
            dialog.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)

            // Show the dialog
            dialog.show()

            // Style the buttons
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(ContextCompat.getColor(context, R.color.blue_start))
                setTypeface(typeface, Typeface.BOLD)
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                setTextColor(ContextCompat.getColor(context, R.color.red_logout))
            }
        } else {
            // Jika tidak ada data yang diisi, langsung kembali tanpa dialog
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    // Fungsi untuk memeriksa apakah setidaknya satu data sudah diisi
    private fun isAnyDataFilled(): Boolean {
        // Menggunakan binding untuk mengakses views
        // Asumsikan binding adalah properti class yang sudah diinisialisasi

        // Periksa apakah EditText tidak kosong atau komponen lain sudah dipilih/diisi
        return binding.inputPatientHeight.text.toString().isNotEmpty() ||
                binding.inputPatientWeight.text.toString().isNotEmpty() ||
                binding.inputPatientAge.text.toString().isNotEmpty() ||
                binding.inputPatientBlood.text.toString().isNotEmpty() ||
                binding.inputPatientSex.text.toString().isNotEmpty() ||
                binding.inputPatientName.text.toString().isNotEmpty()


        // Tambahkan pengecekan untuk semua field input yang ada di form Anda
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}