package com.example.animefy

import android.net.Uri
import android.widget.VideoView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class InputViewModel: ViewModel(){



    private val _selectedVideoUri = MutableLiveData<Uri>()
    val selectedVideoUri: LiveData<Uri> = _selectedVideoUri

    fun prepareVideoInput(videoUri: Uri,previewVid: VideoView) {
        previewVid.setVideoURI(videoUri)
        previewVid.start()
    }



}