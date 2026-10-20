package com.jd.trakker.App.Feed

import com.jd.trakker.App.Feed.db.FeedType

sealed interface FeedEvent {
    data class focus(val item: FeedItem) : FeedEvent
    object unfocus : FeedEvent
    data class changeFeedType(val type: FeedType): FeedEvent
    data class search(val name: String) : FeedEvent
    object closeSearch : FeedEvent
    object refresh : FeedEvent
    object clearRefreshFail: FeedEvent
    object nextPage: FeedEvent
}