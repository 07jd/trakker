package com.jd.trakker.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jd.trakker.R
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.item.getId
import com.jd.trakker.data.local.item.getType
import com.jd.trakker.data.local.tracked.TrackedItem
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.repository.ItemRepository
import com.jd.trakker.ui.feed.Feed
import com.jd.trakker.ui.feed.FeedEvent
import com.jd.trakker.ui.feed.FeedViewModel
import com.jd.trakker.ui.focusedItem.FocusedItem
import com.jd.trakker.ui.focusedItem.FocusedItemViewModel
import com.jd.trakker.ui.settings.SettingsScreen
import com.jd.trakker.ui.settings.SettingsViewModel
import com.jd.trakker.ui.tracked.TrackedEvent
import com.jd.trakker.ui.tracked.TrackedScreen
import com.jd.trakker.ui.tracked.TrackedState
import com.jd.trakker.ui.tracked.TrackedViewModel

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
private fun DisplayScreenOrFocusedItem(
    innerPadding: PaddingValues,
    item: Item?,
    trackedItem: TrackedItem?,
    itemRepository: ItemRepository,
    onFocusedItemBackEvent: () -> Unit,
    screen: @Composable (() -> Unit)
) {
    // Main content
    screen()

    // Focused screen
    val content = if(trackedItem != null && item != null) item to trackedItem else null
    AnimatedContent(
        targetState = content,
        transitionSpec = {
            when {
                // screen -> focusedItemScreen
                targetState != null -> slideInHorizontally { it } togetherWith  slideOutHorizontally { -it }
                else                -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        }
    ) { focusedScreenContent ->
        if (focusedScreenContent != null) {
            val (focusedItem, focusedTrackedItem) = focusedScreenContent

            // Create focused item view model
            val focusedItemViewModel: FocusedItemViewModel = viewModel(
                key = "focused_item_${focusedItem.getType()}_${focusedItem.getId()}"
            ) {
                FocusedItemViewModel(
                    item = focusedItem,
                    trackedItem = focusedTrackedItem,
                    itemRepository = itemRepository,
                    goBack = { onFocusedItemBackEvent() }
                )
            }

            val focusedItemUiState by focusedItemViewModel.uiState.collectAsStateWithLifecycle()
            FocusedItem(
                innerRootPadding = innerPadding,
                uiState = focusedItemUiState,
                onEvent = focusedItemViewModel::onEvent,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
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
                        val trackedViewModel = viewModel{ TrackedViewModel(viewModel.itemRepository) }
                        val uiState by trackedViewModel.uiState.collectAsStateWithLifecycle(initialValue = TrackedState())

                        val gridState = rememberLazyGridState()
                        val verticalScrollState = rememberScrollState()
                        val searchBarState = rememberTextFieldState()
                        // Status + Favorite
                        val horizontalScrollStates = (0..<(TrackedStatus.entries.size + 1)).map { rememberLazyListState() }

                        DisplayScreenOrFocusedItem(
                            innerPadding            = innerPadding,
                            item                    = uiState.focusedItem,
                            trackedItem             = uiState.focusedItemTracked,
                            itemRepository          = viewModel.itemRepository,
                            onFocusedItemBackEvent  = { trackedViewModel.onEvent(TrackedEvent.Unfocus) },
                            screen = {
                                TrackedScreen(
                                    innerRootPadding = innerPadding,
                                    scrollState = verticalScrollState,
                                    horizontalScrollStates = horizontalScrollStates,
                                    uiState = uiState,
                                    gridState = gridState,
                                    searchBarState = searchBarState,
                                    onEvent = trackedViewModel::onEvent,
                                    onBack = { onBack() }
                                )
                            }
                        )
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


                        DisplayScreenOrFocusedItem(
                            innerPadding = innerPadding,
                            item = uiState.focused,
                            trackedItem = uiState.focusedTracked,
                            itemRepository = viewModel.itemRepository,
                            onFocusedItemBackEvent = { feedViewModel.onEvent(FeedEvent.Unfocus) },
                            screen = {
                                Feed(
                                    innerPadding,
                                    uiState,
                                    gridState,
                                    searchBarState,
                                    feedViewModel::onEvent,
                                    onBack = { onBack() }
                                )
                            }
                        )
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

