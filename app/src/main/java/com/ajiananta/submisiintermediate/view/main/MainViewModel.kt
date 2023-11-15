package com.ajiananta.submisiintermediate.view.main


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajiananta.submisiintermediate.data.StoriesRepos
import java.io.File

class MainViewModel(private val storiesRepos: StoriesRepos) : ViewModel() {
    val coordinatLatitude = MutableLiveData(0.0)
    val coordinatLongitude = MutableLiveData(0.0)

    fun getAllStories(token: String) = storiesRepos.getStories(token)
    fun uploadStories(token: String, imageFile: File, desc: String, lat: String?, lon: String?) = storiesRepos.uploadStories(token, imageFile, desc, lat, lon)
    fun getListMapsStories(token: String) = storiesRepos.getMapsStories(token)
}