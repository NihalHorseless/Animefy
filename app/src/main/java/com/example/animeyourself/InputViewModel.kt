package com.example.animeyourself

import android.app.Application
import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.annotation.MainThread
import androidx.lifecycle.*
import java.util.concurrent.atomic.AtomicBoolean


class InputViewModel(application: Application) : AndroidViewModel(application) {


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