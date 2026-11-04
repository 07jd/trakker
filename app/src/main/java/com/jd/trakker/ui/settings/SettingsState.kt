package com.jd.trakker.ui.settings

import com.jd.trakker.ui.Destination
import com.jd.trakker.ui.theme.ThemeType

data class SettingsState(
    val errorMessage: String? = null,
    val cachedImagesSize: Long = 0L,

    // Data is going to be populated by the PreferencesManager
    val useVibrations: Boolean = true,
    val theme: ThemeType = ThemeType.AUTO,
    val useDynamicColor: Boolean = true,
    val gameFeedEnabled: Boolean = true,
    val movieFeedEnabled: Boolean = true,
    val showFeedEnabled: Boolean = true,
    val initialScreen: Destination = Destination.Tracked
)