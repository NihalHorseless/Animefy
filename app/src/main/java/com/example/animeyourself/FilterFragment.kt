package com.example.animeyourself

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.animeyourself.databinding.FragmentFilterBinding
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private val viewModel: FilterViewModel by viewModels()

    //Fields
    private lateinit var filteredVid: VideoView
    private lateinit var saveBtn: FloatingActionButton
    private lateinit var filterOptions: ChipGroup


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
        val videoUri = arguments?.getString("videoUri")

        // Set the video URI as the data source for the VideoView
        filteredVid.setVideoURI(videoUri?.toUri())

        // Start playing the video
        filteredVid.start()
    }

}


