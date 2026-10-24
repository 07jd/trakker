package com.jd.trakker.data.local.preferences

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.appDataStore by preferencesDataStore(
    name = "settings",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

object Preferences {
    // Initial setup
    val isInitalSetupDone = booleanPreferencesKey("initialSetup")

    // Overall App Settings
    val initialAppScreen  = stringPreferencesKey("initialAppScreen")
    val useVibrations     = booleanPreferencesKey("useVibrations")

    // App theme settings
    val themeType         = stringPreferencesKey("themeType")
    val useDynamicColor   = booleanPreferencesKey("useDynamicColor")

    // Feed settings
    val gameFeedEnabled    = booleanPreferencesKey("gameFeedEnabled")
    val movieFeedEnabled   = booleanPreferencesKey("movieFeedEnabled")
    val showFeedEnabled    = booleanPreferencesKey("showFeedEnabled")
}