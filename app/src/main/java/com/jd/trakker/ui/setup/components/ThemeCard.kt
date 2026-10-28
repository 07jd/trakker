package com.jd.trakker.ui.setup.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.setup.SetupEvent
import com.jd.trakker.ui.theme.ThemeType
import com.jd.trakker.ui.theme.getPrimaryAndSecondaryColors
import com.jd.trakker.ui.theme.toStringRes

@Composable
fun ThemeCards(
    currentTheme: ThemeType,
    dynColor: Boolean,
    useVibrations: Boolean,
    onEvent: (SetupEvent) -> Unit)
{
    val haptics = LocalHapticFeedback.current

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ThemeType.entries.forEach { theme ->
            val selected = theme == currentTheme
            val (primary, secondary) = getPrimaryAndSecondaryColors(theme, dynColor)

            Card(
                modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                            onEvent(SetupEvent.ChangeTheme(theme))
                        }, shape = RoundedCornerShape(16.dp), border = if (selected) {
                    BorderStroke(
                        2.dp, MaterialTheme.colorScheme.primary
                    )
                } else null, colors = CardDefaults.cardColors(
                    containerColor = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Color preview
                    Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp)
                                .clip(RoundedCornerShape(10.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(primary)
                        )
                        Box(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(secondary)
                        )
                    }

                    // Theme name
                    Text(
                        text = stringResource(theme.toStringRes()),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    // Selection indicator
                    Box(
                        modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ), contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Text(
                                text = stringResource(R.string.checkmark),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}