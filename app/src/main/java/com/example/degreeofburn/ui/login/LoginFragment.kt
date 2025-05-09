package com.example.degreeofburn.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.R
import com.example.degreeofburn.data.repository.LoginRepository
import com.example.degreeofburn.databinding.FragmentLoginBinding
import com.example.degreeofburn.ui.home.MainActivity
import com.example.degreeofburn.ui.register.RegisterActivity
import com.example.degreeofburn.utils.Resource
import com.example.degreeofburn.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager
    private var loginSuccessful = false
    private var isPasswordVisible = false

    override fun getTheme() = R.style.BottomSheetTransparent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SessionManager
        sessionManager = SessionManager(requireContext())

        // Initialize ViewModel
        val repository = LoginRepository()
        val factory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        // Set expanded height for the bottom sheet
        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        // Set up password visibility toggle
        setupPasswordToggle()

        // Observe login result
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading indicator
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Loading..."
                }
                is Resource.Success -> {
                    // Hide loading indicator
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = getString(R.string.login_button)

                    result.data?.let { loginResponse ->
                        // Check if login was successful based on response
                        if (loginResponse.message == "Login successful") {
                            Toast.makeText(requireContext(), loginResponse.message, Toast.LENGTH_SHORT).show()
                            Log.d("LoginFragment", "Login successful, preparing to navigate")

                            // Save user data to session with actual values from API response
                            sessionManager.setLoggedIn(true)
                            sessionManager.saveAuthToken(loginResponse.token)
                            sessionManager.saveUserId(loginResponse.uid)

                            // If your API response doesn't include a name field, you might need to
                            // handle this differently or fetch user details in a separate call
                            // For now, we'll use the user ID as the name if needed
                            val userName = loginResponse.uid
                            sessionManager.saveUserName(userName)

                            loginSuccessful = true

                            Log.d("SessionManager", "Token: ${sessionManager.getAuthToken()}")
                            Log.d("SessionManager", "User ID: ${sessionManager.getUserId()}")
                            Log.d("SessionManager", "Is Logged In: ${sessionManager.isLoggedIn()}")
                            Log.d("SessionManager", "Is Session Valid: ${sessionManager.isSessionValid()}")
                            // Handle navigation separately
                            handleNavigation()
                        } else {
                            // API returned success=false
                            Toast.makeText(requireContext(), loginResponse.message, Toast.LENGTH_SHORT).show()
                            Log.e("LoginFragment", "Login failed: ${loginResponse.message}")
                        }
                    }
                }
                is Resource.Error -> {
                    // Hide loading indicator
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = getString(R.string.login_button)

                    // Show error message
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    Log.e("LoginFragment", "Login error: ${result.message}")
                }

                else -> {}
            }
        }

        // Set up login button
        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmailReg.text.toString().trim()
            val password = binding.inputPasswordReg.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // For testing purposes, log these values
            Log.d("LoginFragment", "Attempting login with email: $email")

            // Proceed with login
            viewModel.login(email, password)
        }

        // Set up registration link
        binding.tvRegister.setOnClickListener {
            Log.d("TextLinkReg", "Text link clicked")
            try {
                val intent = Intent(requireActivity(), RegisterActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Set up forgot password link to WhatsApp via browser
        binding.tvForgotPassword.setOnClickListener {
            try {
                val phoneNumber = "6285256544834"
                val message = """
                    *Subjek:* Permohonan Reset Password – Aplikasi Degree of Burn

                    Yth. Admin Aplikasi Degree of Burn,
                    Saya ingin menyampaikan bahwa saya mengalami kendala lupa kata sandi untuk mengakses aplikasi Degree of Burn. Berikut saya lampirkan data diri sebagai verifikasi:
                    * *Email :*
                    * *Nama Lengkap :*
                    Mohon bantuannya untuk proses reset password. Atas perhatian dan bantuannya, saya ucapkan terima kasih.

                    Hormat saya, 
                    [Nama Anda]
                """.trimIndent()

                // URL encode the message
                val encodedMessage = Uri.encode(message)

                // Create WhatsApp web URL with the phone number and message
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$encodedMessage")

                // Create intent that will open in browser
                val intent = Intent(Intent.ACTION_VIEW, uri)

                // Launch browser with WhatsApp web link
                startActivity(intent)

                Log.d("LoginFragment", "Opening WhatsApp web link in browser")
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginFragment", "Error opening WhatsApp web link: ${e.message}", e)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        // Set initial state (password hidden)
        binding.inputPasswordReg.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.inputPasswordReg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)

        // Set click listener dengan area yang lebih besar
        binding.inputPasswordReg.setOnTouchListener { _, event ->
            val drawableRight = 2 // Index for drawableEnd/Right

            // Perluas area klik menjadi 48dp dari ujung kanan
            val touchAreaWidthInPixels = (48 * resources.displayMetrics.density).toInt()

            // Check if the touch was in the expanded touch area
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.inputPasswordReg.right - touchAreaWidthInPixels)) {
                    // Toggle password visibility
                    isPasswordVisible = !isPasswordVisible

                    if (isPasswordVisible) {
                        // Show password
                        binding.inputPasswordReg.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.inputPasswordReg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    } else {
                        // Hide password
                        binding.inputPasswordReg.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.inputPasswordReg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
                    }

                    // Maintain cursor position
                    binding.inputPasswordReg.setSelection(binding.inputPasswordReg.text.length)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun handleNavigation() {
        // This separates navigation from the login process
        try {
            Log.d("LoginFragment", "Starting navigation to MainActivity")

            // Create an intent and pass it to a handler to run after a slight delay
            // This can help avoid issues with lifecycle states
            binding.root.post {
                try {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    Log.d("LoginFragment", "Navigation complete")
                    dismiss()
                } catch (e: Exception) {
                    Log.e("LoginFragment", "Error during delayed navigation: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e("LoginFragment", "Error during navigation setup: ${e.message}", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // If login was successful but navigation didn't happen yet, try one more time
        if (loginSuccessful) {
            try {
                val intent = Intent(requireContext().applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                requireContext().applicationContext.startActivity(intent)
            } catch (e: Exception) {
                Log.e("LoginFragment", "Final navigation attempt failed: ${e.message}", e)
            }
        }
    }
}