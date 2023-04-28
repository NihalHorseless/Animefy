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
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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

    private val videoChooser =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                videoInputUri = it
                prepareVideoInput(it)
                previewVid.visibility = View.VISIBLE
            }
        }

    private val videoRecorder =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    videoInputUri = uri
                    viewModel.selectVideoUri(uri)
                }
                previewVid.visibility = View.VISIBLE
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
            if (previewVid.isVisible) {
                val videoUri = videoInputUri.toString()
                val bundle = bundleOf("videoUri" to videoUri)
                binding.root.findNavController().navigate(R.id.action_inputFragment_to_filterFragment,bundle)
            }
            else
                Toast.makeText(requireActivity(),"Enter a Video!",Toast.LENGTH_SHORT).show()
        }

        viewModel.selectedVideoUri.observe(viewLifecycleOwner) { uri ->
            videoInputUri = uri
            if (videoInputUri != null) {
                prepareVideoInput(videoInputUri!!)
            }
        }

    }

    private fun launchVideoChooser() {
        videoChooser.launch("video/*")
    }

    private fun launchVideoRecorder() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        videoRecorder.launch(intent)
    }


    private fun prepareVideoInput(videoUri: Uri) {
        previewVid.setVideoURI(videoUri)
        previewVid.start()
    }
}