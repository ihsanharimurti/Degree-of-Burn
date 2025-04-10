package com.example.degreeofburn.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.degreeofburn.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

//class LoginFragment :  BottomSheetDialogFragment() {
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val v: View = inflater.inflate(
//            R.layout.fragment_login,
//            container, false
//        )
//
//        val algo_button = v.findViewById<Button>(R.id.buttonLogin)
//
//        algo_button.setOnClickListener {
//            Toast.makeText(
//                activity,
//                "First Button Clicked", Toast.LENGTH_SHORT
//            ).show()
//            dismiss()
//        }
//
//        val textLinkReg = v.findViewById<TextView>(R.id.textlinkreg)
//        textLinkReg.setOnClickListener {
//            val intent = Intent(requireContext(), RegisterActivity::class.java)
//            startActivity(intent)
//            dismiss()
//        }
//
//        return v
//    }
//}


//class LoginFragment : BottomSheetDialogFragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_login, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Set expanded height for the bottom sheet
//        dialog?.setOnShowListener { dialog ->
//            val bottomSheetDialog = dialog as BottomSheetDialog
//            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//            bottomSheet?.let {
//                val behavior = BottomSheetBehavior.from(it)
//                behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                behavior.skipCollapsed = true
//            }
//        }
//
//        // Set up login button
////        val loginButton = view.findViewById<Button>(R.id.buttonLogin)
////        loginButton.setOnClickListener {
////            Toast.makeText(requireContext(), "Login Button Clicked", Toast.LENGTH_SHORT).show()
////            dismiss()
////        }
//
//        // Set up registration link with debug toast
//        val textLinkReg = view.findViewById<TextView>(R.id.buttonLogin)
//        textLinkReg.setOnClickListener {
//            Toast.makeText(requireContext(), "Registration Link Clicked", Toast.LENGTH_SHORT).show()
//            try {
//                this.startActivity(Intent(activity, RegisterActivity::class.java))
//
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//}


class LoginFragment : BottomSheetDialogFragment() {

    override fun getTheme() = R.style.BottomSheetTransparent
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // Set up login button
        val loginButton = view.findViewById<Button>(R.id.buttonLogin)
        loginButton.setOnClickListener {
            Log.d("BtnLog", "Btn link clicked")
            Toast.makeText(requireContext(), "Login Button Clicked", Toast.LENGTH_SHORT).show()
            try {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                // Don't dismiss yet to see if navigation works
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Set up registration link with debug toast
        val textLinkReg = view.findViewById<TextView>(R.id.textlinkreg)
        textLinkReg.setOnClickListener {
            Log.d("TextLinkReg", "Text link clicked")
            Toast.makeText(requireContext(), "Registration Link Clicked", Toast.LENGTH_SHORT).show()
            try {
                val intent = Intent(requireActivity(), RegisterActivity::class.java)
                startActivity(intent)
                // Don't dismiss yet to see if navigation works
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}