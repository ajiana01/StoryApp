package com.ajiananta.submisiintermediate.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ajiananta.submisiintermediate.databinding.ActivityDetailStoriesBinding
import com.ajiananta.submisiintermediate.utils.getAddName
import com.ajiananta.submisiintermediate.utils.withDateFormat
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class DetailStoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val name = intent.getStringExtra(NAME)
        val imgUrl = intent.getStringExtra(IMAGE_URL)
        val date = intent.getStringExtra(DATE)
        val desc = intent.getStringExtra(DESCRIPTION)
        val lon = intent.getStringExtra(LONGITUDE)!!.toDouble()
        val lat = intent.getStringExtra(LATITUDE)!!.toDouble()
        val location = getAddName(this@DetailStoriesActivity, lat, lon)

        binding.apply {
            tvDetailName.text = name
            tvImgDate.text = date?.withDateFormat()
            tvDetailDescription.text = desc
            tvLocationPhoto.text = location
            Glide.with(root.context)
                .load(imgUrl)
                .into(ivDetailPhoto)
            floatingActionButton.setOnClickListener {
                finish()
            }
        }
        if (lon == 0.0 && lat == 0.0) {
            binding.ivIconLocation.visibility = View.INVISIBLE
            binding.tvLocationPhoto.visibility = View.INVISIBLE
        } else {
            binding.ivIconLocation.visibility = View.VISIBLE
            binding.tvLocationPhoto.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val NAME = "name"
        const val IMAGE_URL = "imageUrl"
        const val DATE = "date"
        const val DESCRIPTION = "description"
        const val LONGITUDE = "lon"
        const val LATITUDE = "lat"
    }
}