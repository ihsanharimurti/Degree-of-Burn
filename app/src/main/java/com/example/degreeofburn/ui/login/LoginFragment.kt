package com.example.degreeofburn.ui.login

import android.content.Intent
import android.os.Bundle
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
        val dummyToken = "dummy_token_123"
        val dummyUserId = "user_001"
        val dummyUserName= "Siva Maharani"

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
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                        Log.d("LoginFragment", "Login successful, preparing to navigate")

                        // Save user data to session
                        sessionManager.setLoggedIn(true)
                        sessionManager.saveAuthToken(dummyToken)
                        sessionManager.saveUserId(dummyUserId)
                        sessionManager.saveUserName(dummyUserName)

                        loginSuccessful = true

                        // Handle navigation separately
                        handleNavigation()
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

        // Set up forgot password link
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot password feature coming soon", Toast.LENGTH_SHORT).show()
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