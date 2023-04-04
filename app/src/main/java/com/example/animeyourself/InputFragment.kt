package com.example.animeyourself

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.animeyourself.databinding.FragmentInputBinding


class InputFragment : Fragment() {

    //Binding
    private lateinit var binding: FragmentInputBinding

    //Fields
    private lateinit var recordBtn: Button
    private lateinit var chooseBtn: Button
    private lateinit var filterBtn: Button
    private lateinit var previewVid: VideoView


    private lateinit var viewModel: InputViewModel

    private var videoInputUri: Uri? = null


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
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.selectedVideoUri.observe(viewLifecycleOwner) { uri ->
            videoInputUri = uri
            if (videoInputUri != null) {
                prepareVideoInput(videoInputUri!!)
            }
        }
    }

    private fun initializeFields() {
        viewModel = ViewModelProvider(this)[InputViewModel::class.java]

        chooseBtn = binding.chooseVideoBtn
        recordBtn = binding.recordVideoBtn
        filterBtn = binding.animateBtn
        previewVid = binding.videoView

        chooseBtn.setOnClickListener {
            launchVideoChooser()
        }

        recordBtn.setOnClickListener {
            launchVideoRecorder()
        }

        filterBtn.setOnClickListener {
            //  navigateToFilterScreen()
        }

    }

    private fun launchVideoChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_VIDEO_CHOOSER)
    }

    private fun launchVideoRecorder() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, REQUEST_VIDEO_RECORDER)
    }


    private fun prepareVideoInput(videoUri: Uri) {
        previewVid.setVideoURI(videoUri)
        previewVid.start()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_VIDEO_CHOOSER -> {
                    data?.data?.let { uri ->
                        videoInputUri = uri
                        viewModel.selectVideoUri(uri)
                    }
                }
                REQUEST_VIDEO_RECORDER -> {
                    data?.data?.let { uri ->
                        videoInputUri = uri
                        viewModel.selectVideoUri(uri)
                    }
                }
            }
            previewVid.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val REQUEST_VIDEO_CHOOSER = 1
        private const val REQUEST_VIDEO_RECORDER = 2
    }


}