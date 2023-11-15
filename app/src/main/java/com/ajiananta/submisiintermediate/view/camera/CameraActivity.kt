package com.ajiananta.submisiintermediate.view.camera

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.ajiananta.submisiintermediate.R
import com.ajiananta.submisiintermediate.databinding.ActivityCameraBinding
import com.ajiananta.submisiintermediate.utils.createFiles
import com.ajiananta.submisiintermediate.view.upload.UploadStoryActivity

@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var cameraSelect: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imgCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.camerCapture.setOnClickListener {
            takeImg()
        }
        binding.cameraSwitch.setOnClickListener {
            cameraSelect.apply {
                if (this == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideUI()
        startCamera()
    }

    private fun takeImg() {
        val imgCapt = imgCapture ?: return
        val photoImg = createFiles(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoImg).build()
        imgCapt.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        R.string.fail_to_get_image,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent().apply {
                        putExtra(EXTRA_PHOTO, photoImg)
                        putExtra(EXTRA_BACK_CAMERA, cameraSelect == CameraSelector.DEFAULT_BACK_CAMERA)
                    }
                    setResult(UploadStoryActivity.CAMERA_X_RESULT, intent)
                    finish()
                }
            }
        )
    }

    private fun hideUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun startCamera() {
        val cameraProvide = ProcessCameraProvider.getInstance(this)
        cameraProvide.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProvide.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imgCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelect,
                    preview,
                    imgCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    R.string.fail_to_open_camera,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        const val EXTRA_PHOTO = "Photo"
        const val EXTRA_BACK_CAMERA = "backCamera"
    }
}