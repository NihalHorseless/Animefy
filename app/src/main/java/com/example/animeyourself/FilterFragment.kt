package com.example.animeyourself

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.daasuu.gpuv.composer.FillMode
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.composer.Rotation
import com.daasuu.gpuv.egl.filter.GlExposureFilter
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlFilterGroup
import com.daasuu.gpuv.egl.filter.GlPosterizeFilter
import com.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.example.animeyourself.databinding.FragmentFilterBinding
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private val viewModel: FilterViewModel by viewModels()
    private val TAG = "FilterFragment"

    //Fields
    private lateinit var filteredVid: VideoView
    private lateinit var saveBtn: FloatingActionButton
    private lateinit var filterOptions: ChipGroup

    private lateinit var sourceVideoUri: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()


    }

    private fun initializeFields() {

        filteredVid = binding.videoView
        saveBtn = binding.floatingActionButton
        filterOptions = binding.chipGroup
        // Get the URI of the video from the arguments
        sourceVideoUri = arguments?.getString("videoUri")!!.toUri()
        // Creating temp file to reference path
        val tempFile = createTempFileHere(sourceVideoUri)
        val outputFilePath = "${requireContext().cacheDir}/filtered_video.mp4"
        applyFilter(GlPosterizeFilter(), tempFile.path, outputFilePath)
        // Set the video URI as the data source for the VideoView

        saveBtn.setOnClickListener {
            saveVideoToGallery()
        }

    }

    private fun saveVideoToGallery() {
        val contentResolver = requireContext().contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "filtered_video.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        }
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        try {
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    File("${requireContext().cacheDir}/filtered_video.mp4").inputStream()
                        .use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                }
                Toast.makeText(requireContext(), "Video saved to gallery", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving video to gallery", e)
            Toast.makeText(requireContext(), "Error saving video to gallery", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun createTempFileHere(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp", ".mp4", requireContext().cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        return tempFile
    }

    private fun applyFilter(appliedFilter: GlFilter, filePath: String, outputfilePath: String) {
        lifecycleScope.launch {
            GPUMp4Composer(filePath, outputfilePath)
                .rotation(Rotation.NORMAL)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(GlFilterGroup(appliedFilter, appliedFilter,appliedFilter))
                .listener(object : GPUMp4Composer.Listener {
                    override fun onProgress(progress: Double) {
                        Log.d(TAG, "onProgress = $progress")
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "onCompleted()")
                        requireActivity().runOnUiThread {
                            filteredVid.setVideoPath(outputfilePath)
                            filteredVid.start()
                            Toast.makeText(
                                context,
                                "codec complete path =" + outputfilePath,
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }

                    override fun onCanceled() {
                        Log.d(TAG, "onCanceled")
                    }

                    override fun onFailed(exception: Exception?) {
                        Log.e(TAG, "onFailed()", exception)
                    }
                })
                .start()

        }
    }

}


