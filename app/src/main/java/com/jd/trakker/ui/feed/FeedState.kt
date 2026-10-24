package com.jd.trakker.ui.feed

import com.jd.trakker.ui.components.Item
import com.jd.trakker.ui.components.ItemType

data class FeedState(
    val feed: List<Item> = emptyList(),
    val feedType: ItemType = ItemType.Game,
    val pageOffset: Int = 1,
    val nextPageLoading: Boolean = false,

    val loading: Boolean = false,
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