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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.daasuu.gpuv.composer.FillMode
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.composer.Rotation
import com.daasuu.gpuv.egl.filter.*
import com.example.animeyourself.customfilters.GlAnimeFilter
import com.example.animeyourself.customfilters.GlCandyRedFilter
import com.example.animeyourself.customfilters.GlCartoonFilter
import com.example.animeyourself.customfilters.GlOrangeFilter
import com.example.animeyourself.databinding.FragmentFilterBinding
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding

    private val TAG = "FilterFragment"

    //Fields
    private lateinit var filteredVideoView: VideoView
    private lateinit var saveBtn: FloatingActionButton
    private lateinit var filterOptions: ChipGroup
    private lateinit var revertBtn: FloatingActionButton
    private lateinit var sourceVideoUri: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()


    }

    private fun initializeFields() {
        // Arranging color level of the poster filter
        val posterFilter = GlPosterizeFilter()
        posterFilter.setColorLevels(7)
        // Binding objects
        filteredVideoView = binding.videoView
        filteredVideoView.setOnCompletionListener { filteredVideoView.start() }
        saveBtn = binding.saveBtn
        revertBtn = binding.revertBtn

        filterOptions = binding.chipGroup
        // Get the URI of the video from the arguments
        sourceVideoUri = arguments?.getString("videoUri")!!.toUri()
        filteredVideoView.setVideoURI(sourceVideoUri)
        filteredVideoView.start()
        // Creating temp file to reference path
        val tempFile = createTempFileHere(sourceVideoUri)
        val outputFilePath = "${requireContext().cacheDir}/filtered_video.mp4"

        filterOptions.setOnCheckedStateChangeListener { _, checkedId ->
            if (checkedId.size > 0) {
                when (checkedId[0]) {
                    R.id.chipAnime -> {
                        applyFilter(
                            GlFilterGroup(
                                GlCartoonFilter(),
                                GlAnimeFilter(),
                                GlHighlightShadowFilter()
                            ),
                            tempFile.path,
                            outputFilePath
                        )
                    }
                    R.id.chipCandy -> {
                        applyFilter(
                            GlCandyRedFilter(),
                            tempFile.path,
                            outputFilePath
                        )
                    }
                    R.id.chipSepia -> {
                        applyFilter(
                            GlFilterGroup(GlCartoonFilter(), GlAnimeFilter(), GlOrangeFilter()),
                            tempFile.path, outputFilePath
                        )
                    }
                    R.id.chipPoster -> {
                        applyFilter(
                            posterFilter,
                            tempFile.path, outputFilePath
                        )
                    }
                    R.id.chipNeon -> {
                        applyFilter(
                            GlFilterGroup(GlMonochromeFilter(), GlVignetteFilter()),
                            tempFile.path,
                            outputFilePath
                        )
                    }

                }
            }
        }

        saveBtn.setOnClickListener {
            saveVideoToGallery()
        }
        revertBtn.setOnClickListener {
            filteredVideoView.setVideoURI(sourceVideoUri)
            filteredVideoView.start()
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

        binding.root.findNavController().navigate(R.id.action_filterFragment_to_inputFragment)

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

    private fun applyFilter(
        appliedFilter: GlFilter,
        filePath: String,
        outputFilePath: String
    ) {
        lifecycleScope.launch {
            GPUMp4Composer(filePath, outputFilePath)
                .rotation(Rotation.NORMAL)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(appliedFilter)
                .listener(object : GPUMp4Composer.Listener {
                    override fun onProgress(progress: Double) {
                        Log.d(TAG, "onProgress = $progress")
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "onCompleted()")
                        requireActivity().runOnUiThread {
                            filteredVideoView.setVideoPath(outputFilePath)
                            filteredVideoView.start()

                            Toast.makeText(
                                context,
                                "codec complete path =$outputFilePath",
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


