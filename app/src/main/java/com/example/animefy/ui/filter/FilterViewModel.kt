package com.example.animefy.ui.filter

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daasuu.gpuv.egl.filter.*
import com.example.animefy.customfilters.GlCandyRedFilter
import com.example.animefy.customfilters.GlOrangeFilter
import com.example.animefy.customfilters.GlSmoothDefineEdge
import com.example.animeyourself.R
import com.google.android.material.chip.ChipGroup
import java.io.File
import java.io.IOException

class FilterViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "FilterViewModel"

    //Fields
    private val _posterFilter = GlPosterizeFilter().apply { setColorLevels(7) }
    private val posterFilter: GlPosterizeFilter
        get() = _posterFilter

    private val _animeFilter =
        GlFilterGroup(GlSmoothDefineEdge(), GlHighlightShadowFilter(), posterFilter)
    private val animeFilter: GlFilterGroup
        get() = _animeFilter

    private val _mangaFilter = GlFilterGroup(
        GlSmoothDefineEdge(), GlHighlightShadowFilter(),
        posterFilter, GlGrayScaleFilter()
    )
    private val mangaFilter: GlFilterGroup
        get() = _mangaFilter

    fun observeSelectedFilter(filterOptions: ChipGroup): LiveData<GlFilter?> {
        val selectedFilter = MutableLiveData<GlFilter?>()
        filterOptions.setOnCheckedStateChangeListener { _, checkedId ->
            // This ensures that filters work only when selected otherwise it gives out of Index Error
            if (checkedId.size > 0) {
                val filter = when (checkedId[0]) {
                    R.id.chipAnime -> animeFilter
                    R.id.chipCandy -> GlCandyRedFilter()
                    R.id.chipSepia -> GlFilterGroup(GlSmoothDefineEdge(), GlOrangeFilter())
                    R.id.chipPoster -> posterFilter
                    R.id.chipManga -> mangaFilter
                    else -> null
                }

                selectedFilter.value = filter
            }
        }

        return selectedFilter
    }

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