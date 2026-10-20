package com.jd.trakker.App

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.appDataStore by preferencesDataStore(
    name = "appConfig"
)

object AppConfig {
    val setupDone = booleanPreferencesKey("setupDone")
}