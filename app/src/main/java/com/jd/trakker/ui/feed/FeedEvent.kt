package com.jd.trakker.ui.feed

import com.jd.trakker.data.local.feed.FeedType

sealed interface FeedEvent {
    data class focus(val item: FeedItem) : FeedEvent
    object unfocus : FeedEvent
    data class changeFeedType(val type: FeedType): FeedEvent
    data class search(val name: String) : FeedEvent
    object closeSearch : FeedEvent
    object refresh : FeedEvent
    object clearFailure: FeedEvent
    object nextPage: FeedEvent
}