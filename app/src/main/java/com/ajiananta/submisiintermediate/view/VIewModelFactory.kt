package com.ajiananta.submisiintermediate.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ajiananta.submisiintermediate.data.StoriesRepos
import com.ajiananta.submisiintermediate.di.Injection
import com.ajiananta.submisiintermediate.view.login.LoginViewModel
import com.ajiananta.submisiintermediate.view.main.MainViewModel
import com.ajiananta.submisiintermediate.view.signup.SignUpViewModel

class ViewModelFactory(private val storiesRepos: StoriesRepos) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storiesRepos) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(storiesRepos) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storiesRepos) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { INSTANCE = it }
    }
}