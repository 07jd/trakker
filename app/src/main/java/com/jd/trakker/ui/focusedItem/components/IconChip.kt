package com.jd.trakker.ui.focusedItem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun IconChip(
    name: String,
    iconRes: Int? = null
) {
    FilterChip(
        onClick = {},
        selected = false,
        enabled = false,
        leadingIcon = {
            if (iconRes == null) return@FilterChip

            Icon(
                painter = painterResource(iconRes),
                contentDescription = name,
                modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        },
        label = { Text(name) }
    )
}