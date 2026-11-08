package com.jd.trakker.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jd.trakker.R
import com.jd.trakker.data.local.item.getId
import com.jd.trakker.data.local.item.getType
import com.jd.trakker.ui.feed.Feed
import com.jd.trakker.ui.feed.FeedEvent
import com.jd.trakker.ui.feed.FeedViewModel
import com.jd.trakker.ui.focusedItem.FocusedItem
import com.jd.trakker.ui.focusedItem.FocusedItemViewModel
import com.jd.trakker.ui.settings.SettingsScreen
import com.jd.trakker.ui.settings.SettingsViewModel

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

private fun getSlideDirection(
    current: NavDestination?,
    backStackEntry: NavBackStackEntry?
): AnimatedContentTransitionScope.SlideDirection {
    val currentRoute = Destination.entries.find {
        it.route == current?.route
    }

    val oldRoute = Destination.entries.find {
        it.route == backStackEntry?.destination?.route
    }

    return if (oldRoute == null || currentRoute == null) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else if (currentRoute.ordinal > oldRoute.ordinal) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}

@Composable
fun AppNavHost(
    innerPadding: PaddingValues,
    viewModel: AppViewModel,
    controller: NavHostController,
    startDestination: Destination = Destination.Tracked,
    onBack: () -> Unit
) {
    NavHost(
        navController = controller,
        startDestination = startDestination.route,
        enterTransition = { slideIntoContainer(
            getSlideDirection(
                controller.currentDestination,
                controller.previousBackStackEntry
            ))
        },
        exitTransition = { slideOutOfContainer(
            getSlideDirection(
                controller.currentDestination,
                controller.previousBackStackEntry
            ))
        }
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.Tracked -> {
                        // TODO
                    }
                    Destination.Feed -> {
                        val feedViewModel: FeedViewModel = viewModel {
                            FeedViewModel(
                                preferencesFlow = viewModel.preferencesFlow,
                                itemRepository  = viewModel.itemRepository,
                                feedRepository  = viewModel.feedRepository
                            )
                        }

                        // All used on the feed screen
                        val uiState by feedViewModel.uiState.collectAsStateWithLifecycle()
                        val gridState       = rememberLazyGridState()
                        val searchBarState  = rememberTextFieldState()



                        AnimatedContent(targetState = uiState.focused,
                                        transitionSpec = { when {
                                            targetState != null -> slideInHorizontally { it } togetherWith  slideOutHorizontally { -it }
                                            else                -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                                        } }
                        ) { focusedItem ->
                            when {
                                focusedItem != null -> {
                                    val focusedItemViewModel: FocusedItemViewModel = viewModel(
                                        key = "focused_item_${focusedItem.getType()}_${focusedItem.getId()}"
                                    ) {
                                        FocusedItemViewModel(
                                            item = focusedItem,
                                            itemRepository = viewModel.itemRepository,
                                            goBack = { feedViewModel.onEvent(FeedEvent.Unfocus) }
                                        )
                                    }

                                    val focusedItemUiState by focusedItemViewModel.uiState.collectAsStateWithLifecycle()
                                    FocusedItem(
                                        innerRootPadding = innerPadding,
                                        uiState = focusedItemUiState,
                                        useVibrations = uiState.useVibrations,
                                        onEvent = focusedItemViewModel::onEvent
                                    )
                                }
                                else -> Feed(
                                    innerPadding,
                                    uiState,
                                    gridState,
                                    searchBarState,
                                    feedViewModel::onEvent,
                                    onBack = { onBack() }
                                )
                            }
                        }
                    }
                    Destination.Settings -> {
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(
                                itemRepository = viewModel.itemRepository,
                                preferences = viewModel.preferences
                            )
                        }

                        val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
                        SettingsScreen(
                            innerRootPadding = innerPadding,
                            uiState = uiState,
                            onEvent = settingsViewModel::onEvent,
                            onBack = { onBack() }
                        )
                    }
                }
            }
        }
    }
}

