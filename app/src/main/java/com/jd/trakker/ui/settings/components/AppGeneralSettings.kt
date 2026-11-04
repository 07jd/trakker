package com.jd.trakker.ui.settings.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.Destination
import com.jd.trakker.ui.components.InteractiveCard
import com.jd.trakker.ui.components.ThemeCards
import com.jd.trakker.ui.components.ToggleCard
import com.jd.trakker.ui.settings.SettingsEvent
import com.jd.trakker.ui.theme.ThemeType
import com.jd.trakker.ui.theme.isDynamicColorAvailable

@Composable
fun AppGeneralSettings(
    horizontalPadding: Dp = 12.dp,
    theme: ThemeType,
    dynamicColor: Boolean,
    vibrate: Boolean,
    initialScreen: Destination,
    onEvent: (SettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Label
            Text(
                text = stringResource(R.string.setup_app_theme),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Themes
            ThemeCards(
                currentTheme = theme,
                dynColor = dynamicColor,
                useVibrations = vibrate,
                onEvent = { onEvent(SettingsEvent.ChangeTheme(it)) }
            )
        }


        // Dynamic Color card, if available
        if(isDynamicColorAvailable()) {
            ToggleCard(
                title = stringResource(R.string.setup_useSystemColors),
                label = stringResource(R.string.setup_useSystemColors_detailed),
                state = dynamicColor,
                useVibrations = vibrate,
                onStateChange = { onEvent(SettingsEvent.ChangeDynamicColor(it)) }
            )
        }

        // Initial screen option card
        InteractiveCard(title = stringResource(R.string.initial_screen)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                Destination.entries
                        .filter { it != Destination.Settings }
                        .forEach {
                            val selected = it == initialScreen

                            Box(
                                modifier = Modifier
                                        .border(
                                            width = 2.dp,
                                            color = if(selected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            },
                                            shape = RoundedCornerShape(percent = 25)
                                        )
                                        .clickable { onEvent(SettingsEvent.ChangeInitialScreen(it)) }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(it.stringResLabel),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
            }
        }

        // Vibrate option card
        ToggleCard(
            title = stringResource(R.string.vibrations_option),
            state = vibrate,
            onStateChange = { onEvent(SettingsEvent.ChangeVibration(it)) }
        )
    }
}