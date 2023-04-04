package com.example.animeyourself

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.animeyourself.databinding.FragmentInputBinding
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy


class InputFragment : Fragment() {

    //Binding
    private lateinit var binding: FragmentInputBinding

    //Fields
    private lateinit var chooseBtn: Button
    private lateinit var filterBtn: Button
    private lateinit var previewVid: VideoView

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                chooseVideo()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

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

    }

    private fun initializeFields() {
        viewModel = ViewModelProvider(this)[InputViewModel::class.java]

        chooseBtn = binding.videoBtn

        chooseBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                chooseVideo()
            } else {
                permissionRequestLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        viewModel.videoInputEvent.observe(viewLifecycleOwner) { videoUri ->
            videoUri?.let {
                prepareVideoInput(it)
            }
        }

    }

    private fun prepareVideoInput(videoUri: Uri) {
        previewVid.setVideoURI(videoUri)
        previewVid.start()
    }

    companion object {
        const val REQUEST_CODE_CHOOSE = 23
    }

    private fun chooseVideo() {
        Matisse.from(this)
            .choose(MimeType.of(MimeType.MP4))
            .countable(true)
            .maxSelectable(1)
            .capture(true)
            .captureStrategy(
                CaptureStrategy(
                    true,
                    "${requireActivity().packageName}.provider",
                    "video"
                )
            )
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .forResult(REQUEST_CODE_CHOOSE)
    }

    private fun showInputDialog() {
        val options = arrayOf<String>("Record a Video", "Choose from Gallery")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selected Video")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // User selects Record option

                }
                1 -> {
                    // User selects Gallery option

                }
            }
        }
        builder.show()
    }

}