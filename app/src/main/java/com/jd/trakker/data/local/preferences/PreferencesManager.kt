package com.jd.trakker.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.jd.trakker.ui.theme.ThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(
    private val context: Context
) {
    val initialSetupDone: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[Preferences.isInitalSetupDone] ?: false
    }

    val theme: Flow<ThemeType> = context.appDataStore.data.map { preferences ->
        val rawString = preferences[Preferences.themeType]
        ThemeType.entries.firstOrNull { it.name == rawString } ?: ThemeType.AUTO
    }

    val useDynamicColor: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[Preferences.useDynamicColor] ?: false
    }

    suspend fun setInitialSetupDone(state: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[Preferences.isInitalSetupDone] = state
        }
    }

    suspend fun setTheme(theme: ThemeType) {
        context.appDataStore.edit { preferences ->
            preferences[Preferences.themeType] = theme.name
        }
    }

    suspend fun setDynamicColor(state: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[Preferences.useDynamicColor] = state
        }
    }

    // TODO: Do the other set and gets when the interface for them is stable
}