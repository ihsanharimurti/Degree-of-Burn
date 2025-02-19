package com.example.degreeofburn.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.example.degreeofburn.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginFragment :  BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(
            R.layout.fragment_login,
            container, false
        )

        val algo_button = v.findViewById<Button>(R.id.buttonLogin)

        algo_button.setOnClickListener {
            Toast.makeText(
                activity,
                "First Button Clicked", Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }


        return v
    }
}
