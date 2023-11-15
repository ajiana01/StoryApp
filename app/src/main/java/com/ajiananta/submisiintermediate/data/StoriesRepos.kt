package com.ajiananta.submisiintermediate.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ajiananta.submisiintermediate.api.ApiService
import com.ajiananta.submisiintermediate.api.response.FileUploadResponse
import com.ajiananta.submisiintermediate.api.response.ListStoryItem
import com.ajiananta.submisiintermediate.api.response.LoginResponse
import com.ajiananta.submisiintermediate.api.response.LoginResult
import com.ajiananta.submisiintermediate.api.response.RegisterResponse
import com.ajiananta.submisiintermediate.api.response.StoriesResponse
import com.ajiananta.submisiintermediate.data.preference.UserPreference
import java.io.File

class StoriesRepos(
    private val api: ApiService,
    private val pref: UserPreference,
    private val remoteData: RemoteDataSrc
): DataSource {

    override fun getUser(): LiveData<LoginResult> {
        return pref.getSession().asLiveData()
    }

    override fun login(email: String, password: String): LiveData<LoginResponse> {
        val loginResponseStat = MutableLiveData<LoginResponse>()
        remoteData.login(object : RemoteDataSrc.LoginCallback{
            override fun onLogin(loginResponse: LoginResponse) {
                loginResponseStat.postValue(loginResponse)
            }
        }, email, password)
        return loginResponseStat
    }

    override fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<RegisterResponse> {
        val regisResponse = MutableLiveData<RegisterResponse>()
        remoteData.register(object : RemoteDataSrc.RegisterCallback{
            override fun onRegister(registerResponse: RegisterResponse) {
                regisResponse.postValue(registerResponse)
            }
        }, name, email, password)
        return regisResponse
    }

    override fun uploadStories(
        token: String,
        imageFile: File,
        desc: String,
        lat: String?,
        lon: String?
    ): LiveData<FileUploadResponse> {
        val uploadResponseStat = MutableLiveData<FileUploadResponse>()
        remoteData.uploadStories(object : RemoteDataSrc.UploadStoryCallback{
            override fun onUploadStory(uploadStoryResponse: FileUploadResponse) {
                uploadResponseStat.postValue(uploadStoryResponse)
            }
        }, token, imageFile, desc, lat, lon)
        return uploadResponseStat
    }

    override fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoriesPagingSrc(
                    api = api,
                    dsUserPref = pref
                )
            }
        ).liveData
    }

    override fun getMapsStories(token: String): LiveData<StoriesResponse> {
        val storiesResponseMaps = MutableLiveData<StoriesResponse>()
        remoteData.getMapsStories(object: RemoteDataSrc.GetMapsStoryCallback{
            override fun onGetMapsStory(storiesResponse: StoriesResponse) {
                storiesResponseMaps.postValue(storiesResponse)
            }
        }, token)
        return storiesResponseMaps
    }

    suspend fun logout() {
        pref.logout()
    }

    suspend fun saveSession(name: String, idUser: String, tokenKey: String) {
        pref.saveSession(name, idUser, tokenKey)
    }

    companion object {
        @Volatile
        private var INSTANCE: StoriesRepos? = null
        fun getInstance(
            api: ApiService,
            pref: UserPreference,
            remoteData: RemoteDataSrc
        ): StoriesRepos =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoriesRepos(api, pref, remoteData)
            }.also { INSTANCE = it }
    }
}