package com.jd.trakker.ui.feed

import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.ItemType

data class FeedState(
    // From user preferences
    val useVibrations: Boolean = true,
    val enabledFeeds: List<ItemType> = emptyList(),

    val feed: List<Item> = emptyList(),
    val feedType: ItemType = ItemType.Game,
    val pageOffset: Int = 1,
    val nextPageLoading: Boolean = false,

    val loading: Boolean = true,
    val focused: Item? = null,

    val showingSearchResults: Boolean = false,
    val searchTerm: String = "",  // Needed for refresh search term
    val searchResult: List<Item> = emptyList(),

    val isRefreshing: Boolean = false,
    val operationFailed: Boolean = false,
    val failureMessage: String = "",

    val usingOfflineDataFeed: Boolean = false,
    val usingOfflineDataSearch: Boolean = false,
)