package com.jd.trakker.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.preferences.PreferencesManager
import kotlinx.coroutines.launch

class SetupViewModel(
    private val settings: PreferencesManager
) : ViewModel() {
    fun onEvent(event: SetupEvent) {
        viewModelScope.launch {
            when(event) {
                is SetupEvent.ChangeTheme          -> settings.setTheme(event.theme)
                is SetupEvent.UseSystemColor       -> settings.setDynamicColor(event.state)
                is SetupEvent.ChangeVibrationUsage -> settings.setVibrations(event.state)
                is SetupEvent.ChangeFeedTypes      -> when(event.type) {
                    ItemType.Game  -> settings.setGameFeed(event.state)
                    ItemType.Movie -> settings.setMovieFeed(event.state)
                    ItemType.Show  -> settings.setShowFeed(event.state)
                }
                SetupEvent.Finish                  -> settings.setInitialSetupDone(true)
            }
        }
    }
}