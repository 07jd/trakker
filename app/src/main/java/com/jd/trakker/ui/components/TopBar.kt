package com.jd.trakker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.ui.feed.components.FeedTypeChips
import com.jd.trakker.ui.feed.components.OfflineDataBanner

@Composable
fun TopBar(
    enabledFeeds: List<ItemType>? = null,
    feedType: ItemType? = null,
    showingSearchResults: Boolean,
    usingOfflineData: Boolean = false,
    searchBarState: TextFieldState,
    useVibrations: Boolean = true,
    onFeedTypeChange: ((ItemType) -> Unit)? = null,
    onSearch: (String) -> Unit,
    onCloseSearch: () -> Unit,
    showFilterIcon: Boolean = false,
    filterType: FilterType? = null,
    filterOrder: FilterOrder? = null,
    onApplyFilter: ((FilterOrder, FilterType) -> Unit)? = null,
) {
    val haptics = LocalHapticFeedback.current
    val placeholderText = when(feedType) {
        ItemType.Game -> stringResource(R.string.search_for_game)
        ItemType.Movie -> stringResource(R.string.search_for_movie)
        ItemType.Show -> stringResource(R.string.search_for_show)
        else -> stringResource(R.string.search_generic)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // This is the search box, the material3 provided one is kinda buggy and unstable
        SearchBar(
            showBackButton = showingSearchResults,
            showFilterButton = showFilterIcon && !showingSearchResults,
            placeholderText = placeholderText,
            state = searchBarState,
            filterType = filterType,
            filterOrder = filterOrder,
            onSearch = {
                if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onSearch(it)
            },
            onBack = {
                if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onCloseSearch()
            },
            onApplyFilter = { order, type -> onApplyFilter?.invoke(order, type) }
        )

        if(feedType != null && enabledFeeds != null) {
            // Hide ships when showing search results or only 1 feed type enabled
            AnimatedVisibility(
                visible  = !showingSearchResults && enabledFeeds.size > 1,
                enter    = scaleIn() + fadeIn(),
                exit     = scaleOut() + fadeOut ()
            ) {
                FeedTypeChips(
                    enabledFeeds = enabledFeeds, feedType = feedType, onClick = {
                        if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onFeedTypeChange?.invoke(it)
                    })
            }
        }

        // Just to avoid covers get right against the search bar
        if(enabledFeeds == null || enabledFeeds.size <= 1)
            Spacer(modifier = Modifier.height(12.dp))

        // Inform user that is using local data
        AnimatedVisibility(usingOfflineData) {
            OfflineDataBanner()
        }
    }
}