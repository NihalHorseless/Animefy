package com.example.animefy.ui.filter

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
import com.daasuu.gpuv.egl.filter.GlFilter
import com.example.animefy.FilterFragmentArgs
import com.example.animeyourself.R
import com.example.animeyourself.databinding.FragmentFilterBinding
import kotlinx.coroutines.launch


class FilterFragment : Fragment() {

    private val TAG = "FilterFragment"

    // I defined a boolean variable called canApplyFilter so it won't try to apply multiple filters at the same time
    private var canApplyFilter = false

    private lateinit var binding: FragmentFilterBinding

    private lateinit var viewModel: FilterViewModel

    private lateinit var filteredVideoView: VideoView

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
        val saveButton = binding.saveBtn
        val filterOptions = binding.chipGroup
        val revertButton = binding.revertBtn

        viewModel = ViewModelProvider(this)[FilterViewModel::class.java]

        filteredVideoView = binding.videoView.apply { setOnCompletionListener { start() } }

        // Get the URI of the video from the other Fragment
        val args = FilterFragmentArgs.fromBundle(requireArguments())
        sourceVideoUri = args.videoUri.toUri()
        filteredVideoView.setVideoURI(sourceVideoUri)
        filteredVideoView.start()

        // Observe the User selected filter and then create a temp file for reference
        viewModel.observeSelectedFilter(filterOptions).observe(viewLifecycleOwner) { filter ->
            val tempFile = viewModel.createTempFileHere(sourceVideoUri)
            val outputFilePath = "${requireContext().cacheDir}/$FILTERED_VIDEO_FILENAME"

            filter?.let { applyFilter(it, tempFile.path, outputFilePath) }
        }

        saveButton.setOnClickListener {
            saveVideoToGallery()
        }
        revertButton.setOnClickListener {
            filteredVideoView.setVideoURI(sourceVideoUri)
            filteredVideoView.start()
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


    private fun applyFilter(
        appliedFilter: GlFilter,
        filePath: String,
        outputFilePath: String,
    ) {
        filteredVideoView.stopPlayback()
        if (canApplyFilter) {
            return
        }
        canApplyFilter = true
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
                        canApplyFilter = false
                    }

                    override fun onCanceled() {
                        Log.d(TAG, "onCanceled")
                        canApplyFilter = false
                    }

                    override fun onFailed(exception: Exception?) {
                        Log.e(TAG, "onFailed()", exception)
                        canApplyFilter = false
                    }
                })
                .start()

        }
    }
    companion object {
        const val FILTERED_VIDEO_FILENAME = "filtered_video.mp4"
    }

}


