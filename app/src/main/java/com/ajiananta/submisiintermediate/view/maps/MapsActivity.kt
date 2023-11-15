package com.ajiananta.submisiintermediate.view.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.ajiananta.submisiintermediate.R
import com.ajiananta.submisiintermediate.api.response.ListStoryItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ajiananta.submisiintermediate.databinding.ActivityMapsBinding
import com.ajiananta.submisiintermediate.utils.getAddName
import com.ajiananta.submisiintermediate.view.ViewModelFactory
import com.ajiananta.submisiintermediate.view.login.LoginViewModel
import com.ajiananta.submisiintermediate.view.main.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions


@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private val mainViewModel: MainViewModel by viewModels { factory }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.maps_location)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        loginViewModel.getSession().observe(this) { user ->
            if (user.userId.isNotEmpty()) {
                mainViewModel.getListMapsStories(user.token).observe(this) {
                    markerLocation(it.listStory)
                }
            }
        }

        setMapStyle()

        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("New Marker")
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                    .icon(vectToBitmap(R.drawable.ic_location, Color.parseColor("#3DDC84")))
            )
        }
    }

    private val reqPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getLastLocation()
            }
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLastLocation()
            }
            else -> {
                //No grant
            }
        }
    }

    private fun chckPermission(permissions: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED
    }

    private fun markerLocation(listStory: List<ListStoryItem>) {
        listStory.forEach {storiesMap ->
            if (isValidLatitude(storiesMap.lat)) {
                val latLang = LatLng(storiesMap.lat, storiesMap.lon)
                val addName = getAddName(this, storiesMap.lat, storiesMap.lon)
                mMap.addMarker(MarkerOptions().position(latLang).title(storiesMap.name).snippet(addName))
            }
        }
        getLastLocation()
    }

    private fun isValidLatitude(latitude: Double): Boolean {
        return latitude >= -90.0 && latitude <= 90.0
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (chckPermission(Manifest.permission.ACCESS_FINE_LOCATION) && chckPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    pinMarker(location)
                } else {
                    Toast.makeText(this, getString(R.string.failed_get_location), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            reqPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun pinMarker(location: Location) {
        val targetLocation = LatLng(location.latitude, location.longitude)
        val addName = getAddName(this, location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(targetLocation).title(getString(R.string.your_location)).snippet(addName).icon(vectToBitmap(R.drawable.ic_place, Color.parseColor("#FF0000"))))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15f))
    }

    private fun vectToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0,0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, getString(R.string.style_parsing_failed))
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.can_t_find_style_error), exception)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}