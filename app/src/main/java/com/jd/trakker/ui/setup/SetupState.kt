package com.jd.trakker.ui.setup

import com.jd.trakker.data.local.ItemType
import com.jd.trakker.ui.theme.ThemeType

data class SetupState(
    val theme: ThemeType                = ThemeType.AUTO,
    val useSystemColor: Boolean         = false,
    val useVibrations: Boolean          = true,
    val enabledFeeds: List<ItemType>    = listOf(ItemType.Game, ItemType.Movie, ItemType.Show)
)