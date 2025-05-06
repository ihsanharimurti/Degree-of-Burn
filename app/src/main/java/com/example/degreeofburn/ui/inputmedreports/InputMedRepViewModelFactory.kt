package com.example.degreeofburn.ui.inputmedreports

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.degreeofburn.data.repository.HomeRepository

class InputMedRepViewModelFactory(
    private val application: Application,
    private val repository: HomeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputMedRepViewModel::class.java)) {
            return InputMedRepViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}