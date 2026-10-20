package com.jd.trakker.App.Feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jd.trakker.Api.igdb.db.Game
import com.jd.trakker.App.Feed.db.FeedType
import com.jd.trakker.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

const val cover_img_url =
    "https://images.igdb.com/igdb/image/upload/t_cover_big/" // In aspect ratio of 3:4

@Composable
fun Feed(
    innerPadding: PaddingValues,
    uiState: FeedState,
    onEvent: (FeedEvent) -> Unit
) {
    val gridState = rememberLazyGridState()
    val snackbarState = remember { SnackbarHostState() }
    var previousShowingSearchResults by remember { mutableStateOf(uiState.showingSearchResults) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        if (uiState.focused != null) {
            FocusedScreen(uiState.focused, onEvent)
        } else {
            // Reset grid scroll position on search/back to feed from search
            LaunchedEffect(uiState.showingSearchResults) {
                if (previousShowingSearchResults != uiState.showingSearchResults) {
                    gridState.scrollToItem(0)
                    previousShowingSearchResults = uiState.showingSearchResults
                }
            }

            FeedScreen(
                gridState,
                snackbarState,
                uiState.feed,
                uiState.searchResult,
                uiState.usingOfflineDataFeed,
                uiState.usingOfflineDataSearch,
                uiState.loading,
                uiState.showingSearchResults,
                uiState.nextPageLoading,
                uiState.isRefreshing,
                uiState.refreshFailed,
                uiState.feedType,
                onEvent
            )
        }
    }
}

@Composable
fun FeedScreen(
    gridState: LazyGridState,
    snackbarState: SnackbarHostState,
    feed: List<FeedItem>,
    searchResults: List<FeedItem>,
    usingOfflineDataFeed: Boolean,
    usingOfflineDataSearch: Boolean,
    loading: Boolean,
    showingSearchResults: Boolean,
    nextPageLoading: Boolean,
    isRefreshing: Boolean,
    refreshFailed: Boolean,
    feedType: FeedType,
    onEvent: (FeedEvent) -> Unit
) {
    val loadingBarHeight = 5.dp

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarState) },
        topBar = {
            FeedTopScreen(
                feedType,
                showingSearchResults,
                if (showingSearchResults) usingOfflineDataSearch else usingOfflineDataFeed,
                onEvent
            )
        },
        bottomBar = {
            if (!nextPageLoading) {
                Spacer(modifier = Modifier.height(loadingBarHeight))
            }
            AnimatedVisibility(nextPageLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(loadingBarHeight)
                )
            }
        },
    ) { innerPadding ->
        // Show infinite loading circle
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 4.dp
                )
            }

            return@Scaffold
        }

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onRefresh = {
                onEvent(FeedEvent.refresh)
            },
            isRefreshing = isRefreshing
        ) {
            // Empty Feed/Search Results
            if (showingSearchResults && searchResults.isEmpty() || !showingSearchResults && feed.isEmpty()) {
                val text =
                    if (showingSearchResults) "No results found" else "Nothing to show, try refreshing"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.emoji_frown_fill),
                        contentDescription = "Frown Emoji",
                        modifier = Modifier.size(150.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.displaySmallEmphasized
                    )
                }
            } else {
                // Display cover art
                LazyVerticalGrid(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    state = gridState,
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = if (showingSearchResults) searchResults else feed,
                        key = {
                            when (it) {
                                is FeedGame -> it.game.id
                            }
                        }
                    ) {
                        when (it) {
                            is FeedGame -> FeedGameCard(it.game, onEvent)
                        }
                    }
                }
            }
        }

        // Show info when refresh fails
        LaunchedEffect(refreshFailed) {
            if (refreshFailed) {
                snackbarState.showSnackbar(
                    message = "Refresh failed",
                    duration = SnackbarDuration.Short
                )

                onEvent(FeedEvent.clearRefreshFail)
            }
        }

        // Trigger to load next page of feed when at the bottom of the grid
        LaunchedEffect(gridState) {
            snapshotFlow {
                val totalItems = gridState.layoutInfo.totalItemsCount - 1
                val lastItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                lastItem >= totalItems
            }
                .distinctUntilChanged()
                .filter { it }
                .collect { if (!showingSearchResults && !usingOfflineDataFeed) onEvent(FeedEvent.nextPage) }
        }
    }
}

@Composable
fun FeedScreenChips(feedType: FeedType, onEvent: (FeedEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 5.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FeedType.entries.forEach {
            FilterChip(
                selected = feedType == it,
                label = {
                    Text(it.name)
                },
                onClick = { onEvent(FeedEvent.changeFeedType(it)) },
                leadingIcon = if (feedType == it) {
                    {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.check),
                            contentDescription = "Selected",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun FeedTopScreen(
    feedType: FeedType = FeedType.Games,
    showingSearchResults: Boolean,
    usingOfflineData: Boolean,
    onEvent: (FeedEvent) -> Unit
) {
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val state = rememberTextFieldState()


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // This is the search box, the material3 provided one is kinda buggy and unstable
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (showingSearchResults) 15.dp else 5.dp, horizontal = 15.dp),
            state = state,
            lineLimits = TextFieldLineLimits.SingleLine,
            placeholder = {
                var text = feedType.name.lowercase()
                if (text.last() == 's') text = text.substring(0, text.length - 1)

                Text("Search for a $text")
            },
            shape = MaterialTheme.shapes.small,
            leadingIcon = {
                if (showingSearchResults) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.chevron_double_left),
                        contentDescription = "Selected",
                        modifier = Modifier
                            .size(FilterChipDefaults.IconSize)
                            .clickable {
                                state.clearText()
                                onEvent(FeedEvent.closeSearch)
                            }
                    )
                } else {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        contentDescription = "Search Icon",
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            onKeyboardAction = {
                val userInput = state.text.toString().trim()

                keyboard?.hide()
                focus.clearFocus()

                if (!userInput.isEmpty()) {
                    onEvent(FeedEvent.search(userInput))
                }
            }
        )

        // Hide ships when showing search results
        AnimatedVisibility(!showingSearchResults) {
            FeedScreenChips(feedType, onEvent)
        }

        // Inform user that is using local data
        AnimatedVisibility(usingOfflineData) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Using Offline Data",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun FeedGameCard(game: Game, onEvent: (FeedEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(true) {
                onEvent(FeedEvent.focus(FeedGame(game)))
            },

        ) {
        AsyncImage(
            model = "${cover_img_url}${game.cover}.png",
            contentDescription = "Cover Art of ${game.name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            error = painterResource(id = R.drawable.controller)
        )
    }
}

@Composable
fun FocusedScreen(
    focusedItem: FeedItem,
    onEvent: (FeedEvent) -> Unit
) {
    when (focusedItem) {
        is FeedGame -> {
            val game = focusedItem.game

            Text("This game is focused: ${game.name}")
            Button(
                onClick = {
                    onEvent(FeedEvent.unfocus)
                }
            ) {
                Text("Click here to unfocus")
            }
        }
    }
}