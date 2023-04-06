package com.example.animeyourself

import android.net.Uri
import android.widget.VideoView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class InputViewModel: ViewModel(){


    private var videoUri: Uri? = null

    private val _selectedVideoUri = MutableLiveData<Uri>()
    val selectedVideoUri: LiveData<Uri> = _selectedVideoUri

    fun selectVideoUri(uri: Uri) {
        _selectedVideoUri.value = uri
    }

    fun prepareVideoInput(videoUri: Uri, videoView: VideoView) {
        videoView.setVideoURI(videoUri)
        videoView.start()
    }


}