package com.jd.trakker.ui.components

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun ToggleCard(
    title: String,
    label: String? = null,
    state: Boolean,
    onStateChange: (Boolean) -> Unit,
    useVibrations: Boolean = true
) {
    val haptics = LocalHapticFeedback.current

    InteractiveCard(
        title = title,
        label = label
    ) {
        Switch(
            checked = state,
            onCheckedChange = {
                if (useVibrations) {
                    val hapticsType = when(it) {
                        true -> HapticFeedbackType.ToggleOn
                        else -> HapticFeedbackType.ToggleOff
                    }

                    haptics.performHapticFeedback(hapticsType)
                }

                onStateChange(it)
            }
        )
    }
}