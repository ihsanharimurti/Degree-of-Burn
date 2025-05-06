package com.example.degreeofburn.ui.inputmedreports

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.data.model.PatientDTO
import com.example.degreeofburn.data.repository.HomeRepository
import com.example.degreeofburn.databinding.ActivityInputBinding
import com.example.degreeofburn.databinding.ActivityInputMedicReportsBinding
import com.example.degreeofburn.ui.camera.CameraActivity
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.profile.ProfileViewModel
import com.example.degreeofburn.utils.Resource

class InputMedicReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputMedicReportsBinding
    private val selectedBodyParts = mutableSetOf<String>()
    private lateinit var viewModel: InputMedRepViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMedicReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val factory = InputMedRepViewModelFactory(application, HomeRepository(this))
        viewModel = ViewModelProvider(this, factory)[InputMedRepViewModel::class.java]

        // Load user data
        viewModel.getUserDetail()

        setupToolbar()
        setupBodyPartButtons()
        setupNextButton()
        observeViewModel()
    }


    private fun observeViewModel() {
        // Observer untuk status loading
        viewModel.isLoading.observe(this) { isLoading ->
            // Handle loading state if needed
            // For example, show/hide progress bar
        }

        // Observer untuk detail pengguna
        viewModel.userDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Jangan mengubah teks yang sudah ada ke "..."
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
                    name = binding.inputPatientName.text.toString(),
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
        // Validate patient name
        if (binding.inputPatientName.text.toString().trim().isEmpty()) {
            binding.inputPatientName.error = "Nama pasien harus diisi"
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
        // Asumsikan binding adalah properti class yang sudah diinisialisasi

        // Periksa apakah EditText tidak kosong atau komponen lain sudah dipilih/diisi
        return  binding.inputPatientName.text.toString().isNotEmpty() ||
                selectedBodyParts.isNotEmpty()

        // Tambahkan pengecekan untuk semua field input yang ada di form Anda
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}