package com.jd.trakker.ui.feed.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jd.trakker.R
import com.jd.trakker.data.local.feed.FeedType
import com.jd.trakker.ui.feed.FeedEvent

@Composable
fun TopBar(
    feedType: FeedType,
    showingSearchResults: Boolean,
    usingOfflineData: Boolean,
    onEvent: (FeedEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // This is the search box, the material3 provided one is kinda buggy and unstable
        SearchBar(
            showingSearchResults,
            placeholderText = when(feedType) {
                FeedType.Games  -> stringResource(R.string.search_for_game)
                FeedType.Movies -> stringResource(R.string.search_for_movie)
                FeedType.Shows  -> stringResource(R.string.search_for_show)
            },
            onSearch = { onEvent(FeedEvent.search(it)) },
            onBack   = { onEvent(FeedEvent.closeSearch) }
        )

        // Hide ships when showing search results
        AnimatedVisibility(!showingSearchResults) {
            FeedTypeChips(feedType, onClick = { onEvent(FeedEvent.changeFeedType(it)) } )
        }

        // Inform user that is using local data
        AnimatedVisibility(usingOfflineData) {
            OfflineDataBanner()
        }
    }
}