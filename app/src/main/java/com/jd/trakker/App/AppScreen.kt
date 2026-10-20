package com.jd.trakker.App

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jd.trakker.Api.igdb.client.GameFetcher
import com.jd.trakker.App.Feed.Feed
import com.jd.trakker.App.Feed.feedViewModel
import com.jd.trakker.R

//@DrawableRes
enum class Destination(
    val route: String,
    val label: String,
    val activeIcon: Int,
    val inactiveIcon: Int,
) {
    Setup("setup", "Configuração inicial", 0, 0),
    Favorites("favorite", "Favoritos", R.drawable.heart_fill, R.drawable.heart),
    Media("media", "Media", R.drawable.collection_fill, R.drawable.collection),
    Settings("settings", "Definições", R.drawable.settings_fill, R.drawable.settings)
}

@Composable
fun BottomNavigationBar(controller: NavHostController, startDestination: Destination) {
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    NavigationBar {
        Destination.entries.forEachIndexed { index, destination ->
            if (destination.route == Destination.Setup.route) return@forEachIndexed
            val isDestinationSelected = (selectedDestination == index)

            NavigationBarItem(
                selected = isDestinationSelected,
                onClick = {
                    if (controller.currentDestination?.route != destination.route) {
                        controller.navigate(route = destination.route)
                        selectedDestination = index
                    }
                },
                icon = {
                    val asset = if (isDestinationSelected) {
                        destination.activeIcon
                    } else {
                        destination.inactiveIcon
                    }

                    Icon(
                        imageVector = ImageVector.vectorResource(asset),
                        modifier = Modifier.size(22.dp),
                        contentDescription = ""
                    )
                },
                label = {
                    Text(
                        text = destination.label
                    )
                }
            )
        }
    }
}

@Composable
fun AppNavHost(
    innerPadding: PaddingValues,
    viewModel: AppViewModel,
    controller: NavHostController,
    startDestination: Destination
) {
    NavHost(
        navController = controller,
        startDestination = startDestination.route,
        enterTransition = {
            val current = Destination.entries.find {
                it.route == controller.currentDestination?.route
            }

            val old = Destination.entries.find {
                it.route == controller.previousBackStackEntry?.destination?.route
            }


            val slideDirection = if (old == null || current == null) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else if (current.ordinal > old.ordinal) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }

            slideIntoContainer(slideDirection)
        },
        exitTransition = {
            val current = Destination.entries.find {
                it.route == controller.currentDestination?.route
            }

            val old = Destination.entries.find {
                it.route == controller.previousBackStackEntry?.destination?.route
            }

            val slideDirection = if (old == null || current == null) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else if (current.ordinal > old.ordinal) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }

            slideOutOfContainer(slideDirection)
        }
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.Setup -> TODO()
                    Destination.Favorites -> Home()
                    Destination.Media -> {
                        val feedVM: feedViewModel = viewModel {
                            feedViewModel(
                                feedResultDao = viewModel.getFeedResultDao(),
                                gameDao = viewModel.getGamesDao(),
                                gameFetcher = GameFetcher()
                            )
                        }

                        val uiState by feedVM.uiState.collectAsStateWithLifecycle()
                        Feed(innerPadding, uiState, feedVM::onEvent)
                    }

                    Destination.Settings -> Settings()
                }
            }
        }
    }
}


@Composable
fun AppScreen(
    viewModel: AppViewModel
) {
    val navController = rememberNavController()
    val startDestination = Destination.Favorites
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, startDestination)
        }
    ) { innerPadding ->
        AppNavHost(innerPadding, viewModel, navController, startDestination)
    }
}

@Composable
fun Home() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    ) {}
}

@Composable
fun Settings() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {}
}
