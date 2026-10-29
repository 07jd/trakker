package com.jd.trakker.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jd.trakker.ui.feed.Feed
import com.jd.trakker.ui.feed.FeedViewModel
import com.jd.trakker.R
import com.jd.trakker.ui.feed.FeedEvent
import com.jd.trakker.ui.focusedItem.FocusedItem

enum class Destination(
    val route: String,
    val stringResLabel: Int,
    val activeIcon: Int,
    val inactiveIcon: Int,
) {
    Tracked("tracked", R.string.navbar_tracked_label, R.drawable.heart_fill, R.drawable.heart),
    Feed("feed", R.string.navbar_feed_label, R.drawable.collection_fill, R.drawable.collection),
    Settings("settings", R.string.navbar_settings_label, R.drawable.settings_fill, R.drawable.settings)
}

@Composable
fun BottomNavigationBar(controller: NavHostController, startDestination: Destination) {
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    NavigationBar {
        Destination.entries.forEachIndexed { index, destination ->
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
                    val asset = if (isDestinationSelected) destination.activeIcon
                                else destination.inactiveIcon

                    Icon(
                        imageVector = ImageVector.vectorResource(asset),
                        modifier = Modifier.size(20.dp),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(destination.stringResLabel)) }
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
                    Destination.Tracked -> TempScreen()
                    Destination.Feed -> {
                        val feedViewModel: FeedViewModel = viewModel {
                            FeedViewModel(
                                preferences = viewModel.preferences,
                                itemRepository = viewModel.itemRepository,
                                feedRepository = viewModel.feedRepository
                            )
                        }

                        val uiState by feedViewModel.uiState.collectAsStateWithLifecycle()

                        AnimatedContent(targetState = uiState.focused,
                                        transitionSpec = { when {
                                            targetState != null -> slideInHorizontally { it } togetherWith  slideOutHorizontally { -it }
                                            else                -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                                        } }
                        ) { focusedItem ->
                            when {
                                focusedItem != null -> {
                                    // TODO: make view model and state

                                    FocusedItem(innerPadding, focusedItem, { feedViewModel.onEvent(FeedEvent.unfocus) })
                                }
                                else -> Feed(innerPadding, uiState, feedViewModel::onEvent)
                            }
                        }
                    }
                    Destination.Settings -> TempScreen()
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
    val startDestination by viewModel.preferences.initialAppScreen.collectAsStateWithLifecycle(Destination.Tracked)
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, startDestination)
        }
    ) { innerPadding ->
        AppNavHost(innerPadding, viewModel, navController, startDestination)
    }
}

@Composable
fun TempScreen() {
    Box(
        modifier = Modifier
                .fillMaxSize()
                .background(Color.Red)
    ) {
        Text("TODO!")
    }
}
