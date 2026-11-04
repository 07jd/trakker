package com.jd.trakker.ui.feed.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import com.jd.trakker.R
import com.jd.trakker.data.local.ItemType
import com.jd.trakker.ui.feed.FeedEvent

@Composable
fun TopBar(enabledFeeds: List<ItemType>,
           feedType: ItemType,
           showingSearchResults: Boolean,
           usingOfflineData: Boolean,
           searchBarState: TextFieldState,
           useVibrations: Boolean = true,
           onEvent: (FeedEvent) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxWidth()) {
        // This is the search box, the material3 provided one is kinda buggy and unstable
        SearchBar(
            showingSearchResults,
            placeholderText = when(feedType) {
                ItemType.Game  -> stringResource(R.string.search_for_game)
                ItemType.Movie -> stringResource(R.string.search_for_movie)
                ItemType.Show  -> stringResource(R.string.search_for_show)
            },
            searchBarState,
            onSearch = { if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress); onEvent (FeedEvent.Search(it)) },
            onBack   = { if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress); onEvent(FeedEvent.CloseSearch) }
        )

        // Hide ships when showing search results or only 1 feed type enabled
        AnimatedVisibility(
            visible  = !showingSearchResults && enabledFeeds.size > 1,
            enter    = slideInVertically(),
            exit     = slideOutVertically()
        ) {
            FeedTypeChips(enabledFeeds, feedType, onClick = {
                if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                onEvent(FeedEvent.ChangeFeedType(it))
            } )
        }

        // Inform user that is using local data
        AnimatedVisibility(usingOfflineData) {
            OfflineDataBanner()
        }
    }
}