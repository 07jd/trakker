package com.jd.trakker.ui.setup

import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.ui.theme.ThemeType

sealed interface SetupEvent {
    data class ChangeTheme(val theme: ThemeType)                        : SetupEvent
    data class UseSystemColor(val state: Boolean)                       : SetupEvent
    data class ChangeVibrationUsage(val state: Boolean)                 : SetupEvent
    data class ChangeFeedTypes(val type: ItemType, val state: Boolean)  : SetupEvent
    object Finish                                                       : SetupEvent
}