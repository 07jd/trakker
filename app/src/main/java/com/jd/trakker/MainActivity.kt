package com.jd.trakker

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jd.trakker.App.AppScreen
import com.jd.trakker.App.AppViewModel
import com.jd.trakker.App.appDataStore
import com.jd.trakker.Setup.SetupScreen
import com.jd.trakker.Setup.SetupViewModel
import com.jd.trakker.ui.theme.TrakkerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrakkerTheme {
                TrakkerApp()
            }
        }
    }
}

@Composable
fun TrakkerApp() {
    val context = LocalContext.current
    val setupViewModel = remember { SetupViewModel(context.appDataStore) }
    val appViewModel = remember { AppViewModel(
        dataStore   = context.appDataStore,
        application = context.applicationContext as Application
    ) }

    // Check if first boot, enter setup mode
    val setupDone by appViewModel.setupDone.collectAsStateWithLifecycle(initialValue = null)
    if (setupDone == null) {
        // This is to avoid flashing the screen with the setup screen
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
    } else if (setupDone != true) {
        SetupScreen(setupViewModel)
    } else {
        AppScreen(appViewModel)
    }
}