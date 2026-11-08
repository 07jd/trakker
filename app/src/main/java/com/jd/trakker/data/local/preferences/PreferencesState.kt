package com.jd.trakker.data.local.preferences

import com.jd.trakker.ui.Destination
import com.jd.trakker.ui.theme.ThemeType

data class PreferencesState(
    val isInitialSetupDone: Boolean     = false,
    val initialAppScreen: Destination   = Destination.Tracked,
    val useVibrations: Boolean          = true,
    val themeType: ThemeType            = ThemeType.AUTO,
    val useDynamicColor: Boolean        = false,
    val gameFeedEnabled: Boolean        = true,
    val movieFeedEnabled: Boolean       = true,
    val showFeedEnabled: Boolean        = true
)