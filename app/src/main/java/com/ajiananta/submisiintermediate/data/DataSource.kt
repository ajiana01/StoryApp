package com.ajiananta.submisiintermediate.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.ajiananta.submisiintermediate.api.response.FileUploadResponse
import com.ajiananta.submisiintermediate.api.response.ListStoryItem
import com.ajiananta.submisiintermediate.api.response.LoginResponse
import com.ajiananta.submisiintermediate.api.response.LoginResult
import com.ajiananta.submisiintermediate.api.response.RegisterResponse
import com.ajiananta.submisiintermediate.api.response.StoriesResponse
import java.io.File

interface DataSource {
    fun getUser(): LiveData<LoginResult>
    fun login(email: String, password: String): LiveData<LoginResponse>
    fun register(name: String, email: String, password: String): LiveData<RegisterResponse>
    fun uploadStories(token: String, imageFile: File, desc: String, lat: String?, lon: String?): LiveData<FileUploadResponse>
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>>
    fun getMapsStories(token: String): LiveData<StoriesResponse>
}