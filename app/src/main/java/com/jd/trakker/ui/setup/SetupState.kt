package com.jd.trakker.ui.setup

import com.jd.trakker.ui.theme.ThemeType

data class SetupState(
    val theme: ThemeType        = ThemeType.AUTO,
    val useSystemColor: Boolean = false,
)