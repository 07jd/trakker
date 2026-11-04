package com.jd.trakker.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController

@Composable
fun AppScreen(
    viewModel: AppViewModel,
    navController: NavHostController,
    initialScreen: Destination
) {
    val backStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val startDestination = rememberSaveable{ initialScreen }
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