package com.jd.trakker.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SetupViewModel(
    private val settings: PreferencesManager
) : ViewModel() {
    val uiState = combine(
        settings.theme,
        settings.useDynamicColor
    ) { theme, dynColor ->
        SetupState(
            theme = theme,
            useSystemColor = dynColor
        )
    }

    fun onEvent(event: SetupEvent) {
        viewModelScope.launch {
            when(event) {
                is SetupEvent.changeTheme -> settings.setTheme(event.theme)
                is SetupEvent.useSystemColor -> settings.setDynamicColor(event.state)
                SetupEvent.finish -> settings.setInitialSetupDone(true)
            }
        }
    }
}