package com.jd.trakker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.ItemType
import com.jd.trakker.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    val preferences: PreferencesManager
): ViewModel() {
    val uiState = combine(
        // Feed settings
        combine(
            preferences.gameFeedEnabled,
            preferences.movieFeedEnabled,
            preferences.showFeedEnabled
        ) { gFeed, mFeed, sFeed -> Triple(gFeed, mFeed, sFeed) },
        // General app settings
        combine(
            preferences.useVibrations,
            preferences.theme,
            preferences.useDynamicColor
        ) { vibrate, theme, dynColor -> Triple(vibrate, theme, dynColor) },
        preferences.initialAppScreen
    ) { feedSettings, appSettings, initialScreen ->
        SettingsState(
            useVibrations = appSettings.first,
            theme = appSettings.second,
            useDynamicColor = appSettings.third,
            gameFeedEnabled = feedSettings.first,
            movieFeedEnabled = feedSettings.second,
            showFeedEnabled = feedSettings.third,
            initialScreen = initialScreen
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SettingsState()
    )

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when(event) {
                is SettingsEvent.ChangeDynamicColor -> preferences.setDynamicColor(event.state)
                is SettingsEvent.ChangeVibration    -> preferences.setVibrations(event.state)
                is SettingsEvent.ChangeTheme        -> preferences.setTheme(event.state)
                is SettingsEvent.ChangeFeedType -> when(event.type) {
                    ItemType.Game  -> preferences.setGameFeed(event.state)
                    ItemType.Movie -> preferences.setMovieFeed(event.state)
                    ItemType.Show  -> preferences.setShowFeed(event.state)
                }
                is SettingsEvent.ChangeInitialScreen -> preferences.setInitialAppScreen(event.state)
            }
        }
    }
}