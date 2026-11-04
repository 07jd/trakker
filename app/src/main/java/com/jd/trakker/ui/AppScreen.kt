package com.jd.trakker.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

@Composable
fun AppScreen(
    viewModel: AppViewModel,
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val startDestination by viewModel.preferences.initialAppScreen.collectAsStateWithLifecycle(Destination.Tracked)
    var currentDestination = Destination.entries.find {
        it.route == backStackEntry?.destination?.route
    } ?: startDestination

    Scaffold(
        bottomBar = { AppBottomNavBar(
            currentRoute = currentDestination,
            navigateTo = {
                navController.navigate(it.route){ launchSingleTop = true }
                currentDestination = it
            }
        ) }
    ) { innerPadding ->
        AppNavHost(
            innerPadding,
            viewModel,
            navController,
            startDestination,
            onBack = {
                if(navController.popBackStack()) {
                    currentDestination = Destination.entries.find { it.route == navController.currentDestination?.route }!!
                }
            }
        )
    }
}