package com.jd.trakker.ui.setup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.setup.SetupEvent
import com.jd.trakker.ui.theme.ThemeType
import com.jd.trakker.ui.theme.isDynamicColorAvailable

@Composable
private fun DynamicColorCard(dynColor: Boolean, onEvent: (SetupEvent) -> Unit) {
    // Hide if unsupported
    if(!isDynamicColorAvailable()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info texts
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.setup_useSystemColors),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(R.string.setup_useSystemColors_detailed),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = dynColor,
                onCheckedChange = { onEvent(SetupEvent.useSystemColor(it)) }
            )

        }
    }
}

@Composable
fun ThemeScreen(
    currentTheme: ThemeType,
    dynColor: Boolean,
    onEvent: (SetupEvent) -> Unit)
{
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp, vertical = 25.dp),
           verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Theme selection
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.setup_app_theme),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.setup_choose_app_look),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ThemeCards(currentTheme, dynColor, onEvent = { onEvent(it)} )
        }

        DynamicColorCard(dynColor = dynColor, onEvent = { onEvent(it) })
    }
}