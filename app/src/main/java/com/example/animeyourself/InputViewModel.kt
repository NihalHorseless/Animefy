package com.example.animeyourself

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.MainThread
import androidx.lifecycle.*
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean

class InputViewModel : ViewModel() {

    private val _videoURI = MutableLiveData<URI>()
    val videoURI: LiveData<URI>
        get() = _videoURI
    private val _onVideoSelected = SingleLiveEvent<Uri>()
    val onVideoSelected: LiveData<Uri>
        get() = _onVideoSelected

    fun selectVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        // startActivityForResult
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