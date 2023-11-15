package com.ajiananta.submisiintermediate.di

import android.content.Context
import com.ajiananta.submisiintermediate.api.ApiConfig
import com.ajiananta.submisiintermediate.api.remote.StoriesRoomDb
import com.ajiananta.submisiintermediate.data.RemoteDataSrc
import com.ajiananta.submisiintermediate.data.StoriesRepos
import com.ajiananta.submisiintermediate.data.preference.UserPreference

object Injection {

    fun provideRepository(context: Context): StoriesRepos {
        val apiService = ApiConfig.getApiService()
        val userPref = UserPreference.getInstance(context)
        val remoteData = RemoteDataSrc.getInstance()
        StoriesRoomDb.getDb(context)
        return StoriesRepos.getInstance(apiService, userPref, remoteData)
    }
}