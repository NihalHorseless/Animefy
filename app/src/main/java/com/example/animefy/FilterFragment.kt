package com.example.animefy

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.daasuu.gpuv.composer.FillMode
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.composer.Rotation
import com.daasuu.gpuv.egl.filter.*
import com.example.animefy.customfilters.GlCandyRedFilter
import com.example.animefy.customfilters.GlOrangeFilter
import com.example.animefy.customfilters.GlSmoothDefineEdge
import com.example.animeyourself.R
import com.example.animeyourself.databinding.FragmentFilterBinding
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch



class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding

    private val TAG = "FilterFragment"

    //Fields

    private lateinit var viewModel: FilterViewModel

    private lateinit var filteredVideoView: VideoView

    private lateinit var sourceVideoUri: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[FilterViewModel::class.java]
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()


    }

    private fun initializeFields() {
        val saveButton = binding.saveBtn
        val filterOptions = binding.chipGroup
        val revertButton = binding.revertBtn
        filteredVideoView = binding.videoView.apply { setOnCompletionListener { start() } }
        // Get the URI of the video from the arguments
        val args = FilterFragmentArgs.fromBundle(requireArguments())
        sourceVideoUri = args.videoUri.toUri()
        filteredVideoView.setVideoURI(sourceVideoUri)
        filteredVideoView.start()
        // Creating temp file to reference path
        setupFilterOptions(filterOptions)

        saveButton.setOnClickListener {
            saveVideoToGallery()
        }
        revertButton.setOnClickListener {
            filteredVideoView.setVideoURI(sourceVideoUri)
            filteredVideoView.start()
        }

    }

    private fun setupFilterOptions(filterOptions: ChipGroup) {
        val tempFile = viewModel.createTempFileHere(sourceVideoUri)
        val outputFilePath = "${requireContext().cacheDir}/filtered_video.mp4"

        filterOptions.setOnCheckedStateChangeListener { _, checkedId ->
            // This ensures that filters work only when selected otherwise it gives out of Index Error
            if (checkedId.size > 0) {
                val filter = when (checkedId[0]) {
                    R.id.chipAnime -> viewModel.animeFilter
                    R.id.chipCandy -> GlCandyRedFilter()
                    R.id.chipSepia -> GlFilterGroup(GlSmoothDefineEdge(), GlOrangeFilter())
                    R.id.chipPoster -> viewModel.posterFilter
                    R.id.chipManga -> viewModel.mangaFilter
                    else -> null
                }

                filter?.let { applyFilter(it, tempFile.path, outputFilePath) }
            }
        }

    }

    private fun saveVideoToGallery() {

        if (viewModel.saveVideoToGallery(contentResolver = requireContext().contentResolver, cacheDir = requireContext().cacheDir)
        ) {
            Toast.makeText(requireContext(), "Video saved to gallery", Toast.LENGTH_SHORT).show()
            binding.root.findNavController().navigate(R.id.action_filterFragment_to_inputFragment)
        } else {
            Toast.makeText(requireContext(), "Error saving video to gallery", Toast.LENGTH_SHORT).show()
        }

    }


    // private fun createTempFileHere(uri: Uri): File {
    //     // Add Error handling!
    //     val contentResolver = requireContext().contentResolver
    //     val inputStream = contentResolver.openInputStream(uri)
    //     val tempFile = File.createTempFile("temp", ".mp4", requireContext().cacheDir)
    //     tempFile.outputStream().use { outputStream ->
    //         inputStream?.copyTo(outputStream)
    //     }
    //     inputStream?.close()
    //     return tempFile
    // }

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
                                "Filter Done!",
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


