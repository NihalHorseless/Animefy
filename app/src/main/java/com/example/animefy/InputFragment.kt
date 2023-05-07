package com.example.animefy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.animeyourself.R
import com.example.animeyourself.databinding.FragmentInputBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class InputFragment : Fragment() {

    //Binding
    private lateinit var binding: FragmentInputBinding

    //Fields
    private lateinit var recordBtn: FloatingActionButton
    private lateinit var chooseBtn: FloatingActionButton
    private lateinit var previewVid: VideoView

    private lateinit var viewModel: InputViewModel

    private var videoInputUri: Uri? = null

    private val videoChooser =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                videoInputUri = it
                prepareVideoInput(it)
                navigateToNext()
            }
        }

    private val videoRecorder =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    videoInputUri = uri
                    prepareVideoInput(uri)
                    navigateToNext()
                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()

    }

    override fun onResume() {
        super.onResume()
        previewVid.start()
    }

    private fun initializeFields() {
        viewModel = ViewModelProvider(this)[InputViewModel::class.java]

        chooseBtn = binding.galleryBtn
        recordBtn = binding.cameraBtn

        previewVid = binding.videoView
        // To ensure that background Video plays on repeat
        previewVid.setOnCompletionListener { previewVid.start() }
        previewVid.setVideoPath("android.resource://" + requireContext().packageName + "/" + R.raw.project)
        previewVid.start()

        chooseBtn.setOnClickListener {
            launchVideoChooser()

        }

        recordBtn.setOnClickListener {
            launchVideoRecorder()
        }




        viewModel.selectedVideoUri.observe(viewLifecycleOwner) { uri ->
            videoInputUri = uri
            if (videoInputUri != null) {
                prepareVideoInput(videoInputUri!!)
            }
        }
    }

    private fun navigateToNext() {
        val videoUri = videoInputUri.toString()
        val action = InputFragmentDirections.actionInputFragmentToFilterFragment(videoUri)
        binding.root.findNavController().navigate(action)

    }

    private fun launchVideoChooser() {
        previewVid.pause()
        videoChooser.launch("video/*")
    }

    private fun launchVideoRecorder() {
        previewVid.pause()
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        videoRecorder.launch(intent)
    }


    private fun prepareVideoInput(videoUri: Uri) {
        previewVid.setVideoURI(videoUri)
        previewVid.start()
    }
}