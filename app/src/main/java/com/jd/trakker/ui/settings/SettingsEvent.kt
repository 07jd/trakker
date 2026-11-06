package com.jd.trakker.ui.settings

import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.ui.Destination
import com.jd.trakker.ui.theme.ThemeType
import java.io.OutputStream

sealed interface SettingsEvent {
    data class ChangeVibration(val state: Boolean): SettingsEvent
    data class ChangeDynamicColor(val state: Boolean): SettingsEvent
    data class ChangeTheme(val state: ThemeType): SettingsEvent
    data class ChangeFeedType(val type: ItemType, val state: Boolean): SettingsEvent
    data class ChangeInitialScreen(val state: Destination): SettingsEvent
    data class Backup(val stream: OutputStream?): SettingsEvent
    data class Restore(val data: String?): SettingsEvent
    object ClearFeedbackMessage: SettingsEvent
}