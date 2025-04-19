package com.example.degreeofburn.ui.imageprev

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageResultViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageUri

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }
}