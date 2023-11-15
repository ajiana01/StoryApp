package com.ajiananta.submisiintermediate.data.preference

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ajiananta.submisiintermediate.api.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference (private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session")

    suspend fun saveSession(name: String, idUser: String, tokenKey: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[ID_KEY] = idUser
            preferences[TOKEN_KEY] = tokenKey
        }
    }

    fun getSession(): Flow<LoginResult> {
        return context.dataStore.data.map { preferences ->
            LoginResult(
                preferences[NAME_KEY] ?: "",
                preferences[ID_KEY] ?: "",
                preferences[TOKEN_KEY] ?:"",
            )
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val ID_KEY = stringPreferencesKey("userId")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context)
                INSTANCE = instance
                instance
            }
        }
    }
}