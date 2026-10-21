package com.jd.trakker.App.Feed

import com.jd.trakker.api.igdb.db.Game
import com.jd.trakker.App.Feed.db.FeedType

sealed interface FeedItem
data class FeedGame(val game: Game) : FeedItem

data class FeedState(
    val feed: List<FeedItem> = emptyList(),
    val feedType: FeedType = FeedType.Games,
    val pageOffset: Int = 1,
    val nextPageLoading: Boolean = false,

    val loading: Boolean = false,
    val focused: FeedItem? = null,

    val showingSearchResults: Boolean = false,
    val searchTerm: String = "",  // Needed for refresh search term
    val searchResult: List<FeedItem> = emptyList(),

    val isRefreshing: Boolean = false,
    val refreshFailed: Boolean = false,

    val usingOfflineDataFeed: Boolean = false,
    val usingOfflineDataSearch: Boolean = false,
)