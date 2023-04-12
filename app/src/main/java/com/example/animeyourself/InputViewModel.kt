package com.example.animeyourself

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class InputViewModel: ViewModel(){



    private val _selectedVideoUri = MutableLiveData<Uri>()
    val selectedVideoUri: LiveData<Uri> = _selectedVideoUri

    fun selectVideoUri(uri: Uri) {
        _selectedVideoUri.value = uri
    }



}