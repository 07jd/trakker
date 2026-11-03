package com.jd.trakker.ui.feed

import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.ItemType

sealed interface FeedEvent {
    data class Focus(val item: Item)                : FeedEvent
    object Unfocus                                  : FeedEvent
    data class ChangeFeedType(val type: ItemType)   : FeedEvent
    data class Search(val name: String)             : FeedEvent
    object CloseSearch                              : FeedEvent
    object Refresh                                  : FeedEvent
    object ClearFailure                             : FeedEvent
    object NextPage                                 : FeedEvent
}