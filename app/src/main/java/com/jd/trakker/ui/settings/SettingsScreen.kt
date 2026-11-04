package com.jd.trakker.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jd.trakker.ui.settings.components.AppFeedTypes
import com.jd.trakker.ui.settings.components.AppGeneralSettings
import com.jd.trakker.ui.settings.components.AppInfo
import com.jd.trakker.ui.settings.components.AppStorage

@Composable
fun SettingsScreen(
    innerRootPadding: PaddingValues,
    uiState: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(true) { onBack() }

    Column(
        modifier = Modifier
                .fillMaxSize()
                .padding(innerRootPadding)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        AppInfo(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 26.dp)
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        AppGeneralSettings(
            theme = uiState.theme,
            dynamicColor = uiState.useDynamicColor,
            vibrate = uiState.useVibrations,
            initialScreen = uiState.initialScreen
        ) { onEvent(it) }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        AppFeedTypes(
            uiState.gameFeedEnabled,
            uiState.movieFeedEnabled,
            uiState.showFeedEnabled,
            useVibrations = uiState.useVibrations
        ) { onEvent(it) }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        AppStorage(
            feedBackMessageRes = uiState.feedbackMessage,
            useVibrations = uiState.useVibrations
        ) { onEvent(it) }

        // End padding
        Spacer(modifier = Modifier.padding(top = 32.dp))
    }
}