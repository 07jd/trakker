package com.jd.trakker.ui.setup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.components.ThemeCards
import com.jd.trakker.ui.components.ToggleCard
import com.jd.trakker.ui.setup.SetupEvent
import com.jd.trakker.ui.theme.ThemeType
import com.jd.trakker.ui.theme.isDynamicColorAvailable

@Composable
fun ThemeScreen(
    currentTheme: ThemeType,
    dynColor: Boolean,
    useVibrations: Boolean,
    onEvent: (SetupEvent) -> Unit
) {
    Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp, vertical = 25.dp),
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

            ThemeCards(currentTheme, dynColor, useVibrations, onEvent = { onEvent(SetupEvent.ChangeTheme(it)) })
        }

        // Vibrations ON or OFF card
        ToggleCard(
            title = stringResource(R.string.vibrations_option),
            state = useVibrations,
            onStateChange = { onEvent(SetupEvent.ChangeVibrationUsage(it)) })

        // Dynamic color card
        if(isDynamicColorAvailable()) {
            ToggleCard(
                title = stringResource(R.string.setup_useSystemColors),
                label = stringResource(R.string.setup_useSystemColors_detailed),
                state = dynColor,
                onStateChange = { onEvent(SetupEvent.UseSystemColor(it)) },
                useVibrations = useVibrations
            )
        }
    }
}