package com.jd.trakker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jd.trakker.ui.AppScreen
import com.jd.trakker.ui.AppViewModel
import com.jd.trakker.ui.setup.SetupScreen
import com.jd.trakker.ui.setup.SetupState
import com.jd.trakker.ui.setup.SetupViewModel
import com.jd.trakker.ui.theme.ThemeType
import com.jd.trakker.ui.theme.TrakkerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val viewModel: AppViewModel = viewModel { AppViewModel(context) }
            val appNavController = rememberNavController()

            val setupViewModel: SetupViewModel = viewModel { SetupViewModel(viewModel.preferences) }
            val setupNavController             = rememberNavController()
            val setupUI by setupViewModel.uiState.collectAsStateWithLifecycle(SetupState())

            val theme           by viewModel.preferences.theme.collectAsStateWithLifecycle(ThemeType.AUTO)
            val useDynamicColor by viewModel.preferences.useDynamicColor.collectAsStateWithLifecycle(false)
            val setupDone       by viewModel.preferences.initialSetupDone.collectAsStateWithLifecycle(null)
            val initialScreen   by viewModel.preferences.initialAppScreen.collectAsStateWithLifecycle(null)

            TrakkerTheme(
                type            = theme,
                useDynamicColor = useDynamicColor
            ) {
                when (setupDone) {
                    null, (initialScreen == null) -> Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                    )
                    false -> SetupScreen(setupUI, setupViewModel::onEvent, setupNavController)
                    else -> AppScreen(viewModel, appNavController, initialScreen!!)
                }
            }
        }
    }
}