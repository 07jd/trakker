package com.jd.trakker.ui.settings

import com.jd.trakker.data.local.ItemType
import com.jd.trakker.ui.Destination
import com.jd.trakker.ui.theme.ThemeType

sealed interface SettingsEvent {
    data class ChangeVibration(val state: Boolean): SettingsEvent
    data class ChangeDynamicColor(val state: Boolean): SettingsEvent
    data class ChangeTheme(val state: ThemeType): SettingsEvent
    data class ChangeFeedType(val type: ItemType, val state: Boolean): SettingsEvent
    data class ChangeInitialScreen(val state: Destination): SettingsEvent
}