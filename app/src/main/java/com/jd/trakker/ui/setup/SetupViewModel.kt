package com.jd.trakker.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.ItemType
import com.jd.trakker.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SetupViewModel(
    private val settings: PreferencesManager
) : ViewModel() {
    val uiState = combine(
        combine(
            settings.theme,
            settings.useDynamicColor,
            settings.useVibrations
        ) { theme, dynColor, vibrations -> Triple(theme, dynColor, vibrations) },
        combine(
            settings.gameFeedEnabled,
            settings.movieFeedEnabled,
            settings.showFeedEnabled
        ) { game, movie, show -> buildList {
            if (game) add(ItemType.Game)
            if (movie) add(ItemType.Movie)
            if (show) add(ItemType.Show)
        }}
    ) { (theme, dynColor, vibrations), feeds ->
        SetupState(
            theme = theme,
            useSystemColor = dynColor,
            useVibrations = vibrations,
            enabledFeeds = feeds
        )
    }

    fun onEvent(event: SetupEvent) {
        viewModelScope.launch {
            when(event) {
                is SetupEvent.changeTheme -> settings.setTheme(event.theme)
                is SetupEvent.useSystemColor -> settings.setDynamicColor(event.state)
                is SetupEvent.changeVibrationUsage -> settings.setVibrations(event.state)
                SetupEvent.finish -> settings.setInitialSetupDone(true)
            }
        }
    }
}