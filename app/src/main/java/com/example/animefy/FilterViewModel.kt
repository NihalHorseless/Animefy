package com.example.animefy

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.daasuu.gpuv.egl.filter.GlFilterGroup
import com.daasuu.gpuv.egl.filter.GlGrayScaleFilter
import com.daasuu.gpuv.egl.filter.GlHighlightShadowFilter
import com.daasuu.gpuv.egl.filter.GlPosterizeFilter
import com.example.animefy.customfilters.GlSmoothDefineEdge
import java.io.File
import java.io.IOException

class FilterViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = "FilterViewModel"

    //Fields
    private val _posterFilter = GlPosterizeFilter().apply { setColorLevels(7) }
    val posterFilter: GlPosterizeFilter
        get() = _posterFilter

    private val _animeFilter = GlFilterGroup(GlSmoothDefineEdge(), GlHighlightShadowFilter(), posterFilter)
    val animeFilter: GlFilterGroup
        get() = _animeFilter

    private val _mangaFilter = GlFilterGroup(GlSmoothDefineEdge(), GlHighlightShadowFilter(),
        posterFilter, GlGrayScaleFilter()
    )
    val mangaFilter: GlFilterGroup
        get() = _mangaFilter


    fun createTempFileHere(uri: Uri): File {
        val contentResolver = getApplication<Application>().contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp", ".mp4", getApplication<Application>().cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        inputStream?.close()
        return tempFile
    }
    fun saveVideoToGallery(contentResolver: ContentResolver, cacheDir: File): Boolean {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "filtered_video.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        }

        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        return try {
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    File("${cacheDir}/filtered_video.mp4").inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                true
            } ?: false
        } catch (e: IOException) {
            Log.e(TAG, "Error saving video to gallery", e)
            false
        }
    }

}