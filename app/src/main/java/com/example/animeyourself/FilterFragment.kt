package com.example.animeyourself

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.daasuu.gpuv.composer.FillMode
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.composer.Rotation
import com.daasuu.gpuv.egl.filter.GlBilateralFilter
import com.daasuu.gpuv.egl.filter.GlFilter
import com.example.animeyourself.databinding.FragmentFilterBinding
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


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
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(sourceVideoUri)
        val tempFile = createTempFile("temp", null, requireContext().cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        applyFilter(GlBilateralFilter(), tempFile.path)
        // Set the video URI as the data source for the VideoView
        Log.d(TAG,"Before start")
        filteredVid.setVideoURI(sourceVideoUri)

        // Start playing the video
        filteredVid.start()
        Log.d(TAG,"After start")




    }
    private fun applyFilter(appliedFilter: GlFilter,filePath : String) {
        lifecycleScope.launch {
            GPUMp4Composer(filePath, filePath)
                .rotation(Rotation.ROTATION_90)
                .size(540, 960)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(appliedFilter)
                .listener(object : GPUMp4Composer.Listener {
                    override fun onProgress(progress: Double) {
                        Log.d(TAG, "onProgress = $progress")
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "onCompleted()")
//                    Toast.makeText(context, "codec complete path =$filePath.toString()", Toast.LENGTH_SHORT).show()

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


