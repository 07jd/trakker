package com.jd.trakker.ui.settings

import com.jd.trakker.ui.theme.ThemeType

// Data is going to be populated by the PreferencesManager
data class SettingsState(
    val useVibrations: Boolean = true,
    val theme: ThemeType = ThemeType.AUTO,
    val useDynamicColor: Boolean = true,
    val gameFeedEnabled: Boolean = true,
    val movieFeedEnabled: Boolean = true,
    val showFeedEnabled: Boolean = true
)