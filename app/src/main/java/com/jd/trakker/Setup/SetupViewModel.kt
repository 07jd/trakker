package com.jd.trakker.Setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.App.AppConfig
import com.jd.trakker.Wrappers.ApiResult
import com.jd.trakker.Wrappers.ApiStatus
import com.jd.trakker.Wrappers.AuthResponse
import com.jd.trakker.Wrappers.tmdbWrapper
import kotlinx.coroutines.launch

class SetupViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val tmdb = tmdbWrapper("")
    var tmdbCheckedKey by mutableStateOf("")
        private set
    var tmdbRequestStatus by mutableStateOf(
        ApiResult<AuthResponse>(ApiStatus.INACTIVE, null)
    )
        private set

    fun checkApiKey(token: String) {
        tmdb.setApiToken(token)
        tmdbCheckedKey = token
        viewModelScope.launch {
            tmdbRequestStatus = ApiResult(ApiStatus.LOADING, null)
            tmdbRequestStatus = tmdb.checkAuth()
        }
    }

    fun completeSetup() {
        viewModelScope.launch {
            dataStore.edit { config ->
                config[AppConfig.setupDone] = true
            }
        }
    }
}