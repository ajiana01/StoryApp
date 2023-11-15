package com.ajiananta.submisiintermediate.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ajiananta.submisiintermediate.R
import com.ajiananta.submisiintermediate.databinding.ActivityUploadStoryBinding
import com.ajiananta.submisiintermediate.utils.getAddName
import com.ajiananta.submisiintermediate.utils.reduceImg
import com.ajiananta.submisiintermediate.utils.rotateBitmap
import com.ajiananta.submisiintermediate.utils.uriToFile
import com.ajiananta.submisiintermediate.view.ViewModelFactory
import com.ajiananta.submisiintermediate.view.camera.CameraActivity
import com.ajiananta.submisiintermediate.view.login.LoginViewModel
import com.ajiananta.submisiintermediate.view.main.MainActivity
import com.ajiananta.submisiintermediate.view.main.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

@Suppress("DEPRECATION")
class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mainViewModel: MainViewModel by viewModels { factory }
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var longtitude = 0.0
    private var latitude = 0.0

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!isAllPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.cant_access),
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.gaint_access),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isAllPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!isAllPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnSetLocation.setOnClickListener { onSetLocationButton() }
        binding.buttonAdd.setOnClickListener { onAddButtonClick() }
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launchIntentCameraX.launch(intent)
    }

    private var getImg: File? = null
    private val launchIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val myImg = result.data?.getSerializableExtra("Photo") as File
            val backCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getImg = myImg
            val resultBitmap = rotateBitmap(BitmapFactory.decodeFile(getImg?.path), backCamera)
            binding.imageUpload.setImageBitmap(resultBitmap)
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val select = Intent.createChooser(intent, getString(R.string.select_image))
        launchIntentGallery.launch(select)
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultImg ->
        if (resultImg.resultCode == RESULT_OK) {
            val selectImg: Uri = resultImg.data?.data as Uri
            val myImg = uriToFile(selectImg, this@UploadStoryActivity)
            getImg = myImg
            binding.imageUpload.setImageURI(selectImg)
        }
    }

    private fun onSetLocationButton() {
        longtitude = mainViewModel.coordinatLongitude.value.toString().toDouble()
        latitude = mainViewModel.coordinatLatitude.value.toString().toDouble()
        val loc = getAddName(this@UploadStoryActivity, latitude, longtitude).toString()
        binding.tvLocationPhoto.text = loc
        if (loc !== "null") {
            Toast.makeText(this, "Latitude: $latitude, Longtitude: $longtitude", Toast.LENGTH_SHORT).show()
        }
    }

    private val reqLocationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLocation()
            }
            else -> {
                Toast.makeText(this, getString(R.string.cant_access), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermission(permissions: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permissions
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        if (checkLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkLocationPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    storiesCoordinate(location.latitude, location.longitude)
                } else {
                    storiesCoordinate(0.0, 0.0)
                }
            }
        } else {
            reqLocationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun onAddButtonClick() {
        if (getImg != null) {
            if (binding.edAddDescription.text.toString().isNotEmpty()) {
               val img = reduceImg(getImg as File)
                loginViewModel.getSession().observe(this) { userModel ->
                    mainViewModel.uploadStories(userModel.token, img, binding.edAddDescription.text.toString(), latitude.toString(), longtitude.toString()).observe(this) { resultStories ->
                        if (resultStories.message == "") {
                            showLoading(true)
                        } else {
                            showLoading(false)
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.desc_not_blank), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.image_not_blank), Toast.LENGTH_SHORT).show()
        }
    }

    private fun storiesCoordinate(latitude: Double, longtitude: Double) {
        mainViewModel.coordinatLatitude.postValue(latitude)
        mainViewModel.coordinatLongitude.postValue(longtitude)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.visibility = View.VISIBLE
        } else {
            binding.loadingLayout.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}