package com.jd.trakker.App.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.jd.trakker.ui.theme.ThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManager(
    private val context: Context
) {
    val initialSetupDone: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[Settings.isInitalSetupDone] ?: false
    }

    val theme: Flow<ThemeType> = context.appDataStore.data.map { preferences ->
        val rawString = preferences[Settings.themeType]
        ThemeType.entries.firstOrNull { it.name == rawString } ?: ThemeType.AUTO
    }

    val useDynamicColor: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[Settings.useDynamicColor] ?: false
    }

    suspend fun setInitialSetupDone(state: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[Settings.isInitalSetupDone] = state
        }
    }

    suspend fun setTheme(theme: ThemeType) {
        context.appDataStore.edit { preferences ->
            preferences[Settings.themeType] = theme.name
        }
    }

    suspend fun setDynamicColor(state: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[Settings.useDynamicColor] = state
        }
    }

    // TODO: Do the other set and gets when the interface for them is stable
}