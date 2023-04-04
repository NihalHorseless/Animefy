package com.example.animeyourself

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class InputViewModel(application: Application) : AndroidViewModel(application) {



    private var videoUri: Uri? = null

    private val _videoInputEvent = SingleLiveEvent<Uri>()
    val videoInputEvent: LiveData<Uri>
        get() = _videoInputEvent


    fun onVideoInputSelected(videoUri: Uri) {
        _videoInputEvent.value = videoUri
    }




    companion object {
        const val REQUEST_CODE_CAMERA = 100
        const val REQUEST_CODE_GALLERY = 101
    }

    open class SingleLiveEvent<T> : MutableLiveData<T>() {

        private val mPending = AtomicBoolean(false)

        @MainThread
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            if (hasActiveObservers()) {
                // Only one observer at a time can subscribe to this LiveData object.
                throw IllegalStateException("Multiple observers registered but only one will be notified of changes.")
            }

            super.observe(owner, { t ->
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            })
        }

        @MainThread
        override fun setValue(value: T?) {
            mPending.set(true)
            super.setValue(value)
        }

        /**
         * Clears the pending state of this LiveData object, so that future observers will be notified of changes.
         */
        @MainThread
        fun clearPending() {
            mPending.set(false)
        }
    }
}