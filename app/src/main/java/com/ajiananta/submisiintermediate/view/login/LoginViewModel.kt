package com.ajiananta.submisiintermediate.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajiananta.submisiintermediate.data.StoriesRepos
import kotlinx.coroutines.launch

class LoginViewModel(private val repos: StoriesRepos) : ViewModel() {

    fun getSession() = repos.getUser()

    fun saveSession(user: String, idUser: String, tokenKey: String) {
        viewModelScope.launch {
            repos.saveSession(user, idUser, tokenKey)
        }
    }

    fun login(email: String, password: String) = repos.login(email, password)

    fun logout() {
        viewModelScope.launch {
            repos.logout()
        }
    }
}