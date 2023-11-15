package com.ajiananta.submisiintermediate.view.signup

import androidx.lifecycle.ViewModel
import com.ajiananta.submisiintermediate.data.StoriesRepos

class SignUpViewModel(private val repos: StoriesRepos): ViewModel() {
    fun register(name: String, email: String, password: String) = repos.register(name, email, password)
}