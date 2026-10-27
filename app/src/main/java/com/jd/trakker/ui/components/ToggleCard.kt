package com.jd.trakker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun ToggleCard(
    title: String,
    label: String? = null,
    state: Boolean,
    onStateChange: (Boolean) -> Unit,
    useVibrations: Boolean = true
) {
    val haptics = LocalHapticFeedback.current

    Card(modifier = Modifier.fillMaxWidth(),
         shape = RoundedCornerShape(16.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer))
    {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info texts
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                // Label
                if(label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(checked = state,
                   onCheckedChange = {
                       if(useVibrations) {
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
}