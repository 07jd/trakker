package com.jd.trakker.ui.setup

import com.jd.trakker.data.local.ItemType
import com.jd.trakker.ui.theme.ThemeType

sealed interface SetupEvent {
    data class changeTheme(val theme: ThemeType)                        : SetupEvent
    data class useSystemColor(val state: Boolean)                       : SetupEvent
    data class changeVibrationUsage(val state: Boolean)                 : SetupEvent
    data class changeFeedTypes(val type: ItemType, val state: Boolean)  : SetupEvent
    object finish                                                       : SetupEvent
}