package com.jd.trakker.ui.tracked

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.local.tracked.toStringRes
import com.jd.trakker.ui.components.ItemGrid
import com.jd.trakker.ui.components.LoadingCircle
import com.jd.trakker.ui.components.TopBar
import com.jd.trakker.ui.feed.components.SadScreen
import com.jd.trakker.ui.tracked.components.MainScreen

@Composable
fun TrackedScreen(
    innerRootPadding: PaddingValues,
    uiState: TrackedState,
    gridState: LazyGridState,
    horizontalScrollStates: List<LazyListState>,
    scrollState: ScrollState,
    searchBarState: TextFieldState,
    onEvent: (TrackedEvent) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(true) { onBack() }

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = uiState.viewAllType,
                transitionSpec = {
                    // Main screen -> View All screen
                    if(initialState == null && targetState != null) {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                }
            ) { viewAll ->
                if(viewAll == null) {
                    TopBar(
                        searchBarState = searchBarState,
                        showingSearchResults = (uiState.searchResults != null),
                        showFilterIcon = uiState.searchResults == null,
                        filterType = uiState.filterType,
                        filterOrder = uiState.filterOrder,
                        onApplyFilter = { order, type -> onEvent(TrackedEvent.ChangeFilter(order, type)) },
                        useVibrations = true,
                        onSearch = { onEvent(TrackedEvent.Search(it)) },
                        onCloseSearch = { onEvent(TrackedEvent.CloseSearch) }
                    )
                } else {
                    Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 12.dp)
                                    .clickable { onEvent(TrackedEvent.CloseViewAll) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.chevron_double_left),
                                contentDescription = null,
                            )
                        }

                        Text(
                            text = when (viewAll) {
                                TrackedStatus.COMPLETED.name -> stringResource(TrackedStatus.COMPLETED.toStringRes())
                                TrackedStatus.PENDING.name   -> stringResource(TrackedStatus.PENDING.toStringRes())
                                TrackedStatus.DROPPED.name   -> stringResource(TrackedStatus.DROPPED.toStringRes())
                                else                         -> stringResource(R.string.favorite_plural)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        modifier = Modifier
                .fillMaxSize()
                .padding(innerRootPadding)
    ) { innerPadding ->
        // Loading
        AnimatedVisibility(
            visible = uiState.loading,
            enter   = fadeIn(),
            exit    = fadeOut()
        ) { LoadingCircle() }

        // Content
        AnimatedContent(
            targetState = Pair(uiState.searchResults, uiState.viewAllType),
            transitionSpec = {
                // Main screen -> { searchResults / viewAll }
                if(initialState.first == null && initialState.second == null &&
                   (targetState.first != null || targetState.second != null)
                ) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            }
        ) { (results, viewAll) ->
            when {
                results != null -> {
                    BackHandler(true) { onEvent(TrackedEvent.CloseSearch); searchBarState.clearText() }
                    if (results.isEmpty()) {
                        // No results
                        SadScreen(stringResource(R.string.search_no_results))
                    } else {
                        // Show results
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.TopStart
                        ) {
                            ItemGrid(
                                state = gridState,
                                items = results,
                                onClick = { onEvent(TrackedEvent.Focus(it)) }
                            )
                        }
                    }
                }
                viewAll != null -> {
                    BackHandler(true) { onEvent(TrackedEvent.CloseViewAll) }
                    val items = when(uiState.viewAllType) {
                        TrackedStatus.COMPLETED.name    -> uiState.completedItems
                        TrackedStatus.PENDING.name      -> uiState.pendingItems
                        TrackedStatus.DROPPED.name      -> uiState.droppedItems
                        "favorite"                      -> uiState.favoriteItems
                        else                            -> return@AnimatedContent // Early return to avoid flickering
                    }

                    if (items.isEmpty()) {
                        SadScreen(stringResource(R.string.no_items))
                    } else {
                        Box(
                            modifier = Modifier
                                    .padding(innerPadding)
                                    .padding(8.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            ItemGrid(
                                state = rememberLazyGridState(),
                                items = items,
                                onClick = { onEvent(TrackedEvent.Focus(it)) }
                            )
                        }
                    }
                }
                else -> MainScreen(
                    innerPadding = innerPadding,
                    scrollState = scrollState,
                    horizontalScrollStates = horizontalScrollStates,
                    collapsedCategories = uiState.collapsedCategories,
                    favoriteItems = uiState.favoriteItems,
                    pendingItems = uiState.pendingItems,
                    completedItems = uiState.completedItems,
                    droppedItems = uiState.droppedItems,
                    onEvent = { onEvent(it) }
                )
            }
        }
    }
}