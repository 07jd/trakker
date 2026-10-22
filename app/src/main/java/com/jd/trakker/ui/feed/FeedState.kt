package com.jd.trakker.ui.feed

import com.jd.trakker.data.local.game.Game
import com.jd.trakker.data.local.feed.FeedType
import com.jd.trakker.data.local.movie.Movie
import com.jd.trakker.data.local.show.Show

sealed interface FeedItem
data class FeedGame(val game: Game)     : FeedItem
data class FeedMovie(val movie: Movie)  : FeedItem
data class FeedShow(val show: Show)     : FeedItem

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
    val operationFailed: Boolean = false,
    val failureMessage: String = "",

    val usingOfflineDataFeed: Boolean = false,
    val usingOfflineDataSearch: Boolean = false,
)