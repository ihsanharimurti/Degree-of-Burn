package com.example.degreeofburn.ui.inputmedreports

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.data.model.response.PatientResponse
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.databinding.ActivityInputMedicReportsBinding
import com.example.degreeofburn.ui.camera.CameraActivity
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.inputpatients.adapter.PatientAdapter
import com.example.degreeofburn.utils.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InputMedicReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputMedicReportsBinding
    private val selectedBodyParts = mutableSetOf<String>()
    private lateinit var viewModel: InputMedRepViewModel
    private var selectedPatient: PatientResponse? = null
    private var patientsList = mutableListOf<PatientResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMedicReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val factory = InputMedRepViewModelFactory(application, HomeRepository(this))
        viewModel = ViewModelProvider(this, factory)[InputMedRepViewModel::class.java]

        // Load user data and patients data
        viewModel.getUserDetail()
        viewModel.getPatients()

        setupToolbar()
        setupAutoCompletePatients()
        setupBodyPartButtons()
        setupNextButton()
        setupDate()
        observeViewModel()
    }


    private fun setupAutoCompletePatients() {
        // Disable standard AutoComplete behavior
        binding.inputPatientName.apply {
            isClickable = true
            isFocusable = false
            isCursorVisible = false
        }

        // Create a click listener that will manually handle showing the dropdown
        binding.inputPatientName.setOnClickListener {
            // Create and show a dialog with patient list instead
            showPatientSelectionDialog()
        }
    }

    private fun setupDate() {
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        binding.tvDate.text = currentDate
    }

    private fun showPatientSelectionDialog() {
        if (patientsList.isEmpty()) {
            Toast.makeText(this, "Daftar pasien belum tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        // Create patient name list for the dialog
        val patientNames = patientsList.map { it.nama }.toTypedArray()

        // Create and configure the dialog
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val titleView = TextView(this)
        titleView.text = "Pilih Pasien"
        titleView.setTextColor(Color.BLACK)
        titleView.setTextAppearance(R.style.Txt24PoppinsBold) // Menggunakan style yang sudah dibuat
        builder.setCustomTitle(titleView)


        builder.setItems(patientNames) { _, position ->
            // Set the selected patient
            selectedPatient = patientsList[position]
            binding.inputPatientName.setText(selectedPatient?.nama)
        }

        // Add a cancel button
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the dialog with custom background
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)
        dialog.show()
    }
    private fun observeViewModel() {
        // Observer untuk status loading
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
            // Handle loading state if needed
            // For example, show/hide progress bar
        }

        // Observer untuk detail pengguna
        viewModel.userDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Tunggu sampai data tersedia
                }

                is Resource.Success -> {
                    resource.data?.let { userDetail ->
                        binding.tvOfficerName.text = userDetail.nama
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observer untuk daftar pasien
        viewModel.patientsList.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator if needed
                }

                is Resource.Success -> {
                    resource.data?.let { patients ->
                        patientsList = patients.toMutableList()
                        setupPatientAdapter(patients)
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(this, "Gagal memuat data pasien: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            // Show loading overlay and progress bar
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE

            // Disable all interactive elements
            binding.btnArm.isEnabled = false
            binding.btnBackInput.isEnabled = false
            binding.btnInputNext.isEnabled = false
            binding.btnHead.isEnabled = false
            binding.btnLeg.isEnabled = false
            binding.btnGenital.isEnabled = false
            binding.btnPalm.isEnabled = false
            binding.btnTorso.isEnabled = false

            // Bring the overlay and progress bar to the front
            binding.loadingOverlay.bringToFront()
            binding.progressBar.bringToFront()
        } else {
            // Hide loading overlay and progress bar
            binding.loadingOverlay.visibility = View.GONE
            binding.progressBar.visibility = View.GONE

            // Re-enable all interactive elements
            binding.btnArm.isEnabled = true
            binding.btnBackInput.isEnabled = true
            binding.btnInputNext.isEnabled = true
            binding.btnHead.isEnabled = true
            binding.btnLeg.isEnabled = true
            binding.btnGenital.isEnabled = true
            binding.btnPalm.isEnabled = true
            binding.btnTorso.isEnabled = true
        }
    }
    private fun setupPatientAdapter(patients: List<PatientResponse>) {
        // Gunakan custom adapter
        val adapter = PatientAdapter(
            this,
            R.layout.item_patient_dropdown,
            patients
        )

        binding.inputPatientName.setAdapter(adapter)
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnBackInput.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupNextButton() {
        binding.btnInputNext.setOnClickListener {
            if (validateInputs()) {
                // Create DTO to pass to next activity
                val patientDTO = PatientDTO(
                    name = selectedPatient?.nama ?: "",
                    patientId = selectedPatient?.idPasien ?: 0,
                    age= selectedPatient?.usia ?: 0,
                    weight = selectedPatient?.beratBadan ?: "",
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

    private fun validateInputs(): Boolean {
        // Validate patient selection
        if (selectedPatient == null) {
            showToast("Silakan pilih pasien dari daftar")
            return false
        }

        // Validate at least one body part is selected
        if (selectedBodyParts.isEmpty()) {
            showToast("Pilih satu bagian tubuh yang terkena luka bakar")
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
        return selectedPatient != null || selectedBodyParts.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}