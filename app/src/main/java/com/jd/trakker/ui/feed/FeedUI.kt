package com.jd.trakker.ui.feed

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.components.ItemGrid
import com.jd.trakker.ui.components.LoadingCircle
import com.jd.trakker.ui.feed.components.SadScreen
import com.jd.trakker.ui.feed.components.TopBar

@Composable
fun Feed(
    innerPadding: PaddingValues,
    uiState: FeedState,
    gridState: LazyGridState,
    searchBarState: TextFieldState,
    onEvent: (FeedEvent) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(true) { onBack() }

    val snackbarHostState = remember { SnackbarHostState() }

    var previousFeedType        by remember { mutableStateOf(uiState.feedType) }
    var wasShowingSearchResults by remember { mutableStateOf(uiState.showingSearchResults) }
    var wasLoading              by remember { mutableStateOf(uiState.loading) }
    var wasRefreshing           by remember { mutableStateOf(uiState.isRefreshing) }


    // Reset grid scroll only when new search results appears (they alter the loading state) or feed type changes
    LaunchedEffect(
        uiState.showingSearchResults,
        uiState.loading,
        uiState.feedType,
        uiState.isRefreshing
    ) {
        if (uiState.feedType != previousFeedType ||
            uiState.showingSearchResults != wasShowingSearchResults ||
            uiState.loading != wasLoading ||
            uiState.isRefreshing != wasRefreshing
        ) {
            previousFeedType = uiState.feedType
            wasShowingSearchResults = uiState.showingSearchResults
            wasLoading = uiState.loading
            wasRefreshing = uiState.isRefreshing
            gridState.scrollToItem(0)
        }
    }

    // Show info when something fails
    val feedbackMessage = uiState.failureMessage.toIntOrNull()?.let { stringResource(it) } ?: ""
    LaunchedEffect(uiState.operationFailed) {
        if (uiState.operationFailed) {

            snackbarHostState.showSnackbar(
                message = feedbackMessage, duration = SnackbarDuration.Short
            )

            onEvent(FeedEvent.ClearFailure)
        }
    }

    Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
        FeedScreen(uiState, gridState, snackbarHostState, searchBarState, onEvent)
    }
}

@Composable
private fun FeedScreen(
    uiState: FeedState,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    searchBarState: TextFieldState,
    onEvent: (FeedEvent) -> Unit,
    loadingBarHeight: Dp = 5.dp
) {
    // Close search on back
    BackHandler(uiState.showingSearchResults) { onEvent(FeedEvent.CloseSearch); searchBarState.clearText() }
    val haptics = LocalHapticFeedback.current
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(
                searchBarState = searchBarState,
                enabledFeeds = uiState.enabledFeeds,
                feedType = uiState.feedType,
                showingSearchResults = uiState.showingSearchResults,
                usingOfflineData = uiState.usingOfflineDataSearch || uiState.usingOfflineDataFeed,
                useVibrations = uiState.useVibrations,
                onFeedTypeChange = { onEvent(FeedEvent.ChangeFeedType(it)) },
                onSearch = { onEvent(FeedEvent.Search(it)) },
                onCloseSearch = { onEvent(FeedEvent.CloseSearch) }
            )
        },
        bottomBar = {
            // Display loading bar on bottom when needed
            AnimatedVisibility(
                visible = uiState.nextPageLoading,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(loadingBarHeight)
                )
            }

            if(!uiState.nextPageLoading) {
                Spacer(modifier = Modifier.height(0.dp))
            }
        },
    ) { innerPadding ->
        // Show infinite loading circle
        AnimatedVisibility(
            visible = uiState.loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) { LoadingCircle(innerPadding) }

        // Show item grid or search results
        AnimatedVisibility(
            visible = !uiState.loading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            PullToRefreshBox(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                onRefresh = {
                    if(uiState.useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEvent(FeedEvent.Refresh)
                },
                isRefreshing = uiState.isRefreshing
            ) {
                // PS: I have to do this instead of using directly the variables due to the animation
                //     if I don't do this, it will flash SadScreen() for a brief moment even if it found something
                val onSearchResultsScreen = uiState.showingSearchResults && !uiState.loading
                when {
                    // Show search results
                    onSearchResultsScreen && !uiState.searchResult.isEmpty() -> ItemGrid(
                        gridState,
                        items = uiState.searchResult,
                        onClick = { onEvent(FeedEvent.Focus(it)) }
                    )
                    // Show feed
                    !onSearchResultsScreen && !uiState.feed.isEmpty() -> ItemGrid(
                        gridState,
                        items = uiState.feed,
                        onClick = {
                            if (uiState.useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                            onEvent(FeedEvent.Focus(it))
                        },
                        onLoadMore = { if(!uiState.usingOfflineDataFeed) onEvent(FeedEvent.NextPage) }
                    )
                    else -> {
                        val text = if (uiState.showingSearchResults)
                            stringResource(R.string.search_no_results)
                        else
                            stringResource(R.string.feed_empty)

                        SadScreen(text)
                    }
                }
            }
        }
    }
}