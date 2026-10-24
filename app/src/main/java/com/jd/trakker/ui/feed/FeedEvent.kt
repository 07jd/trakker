package com.jd.trakker.ui.feed

import com.jd.trakker.ui.components.Item
import com.jd.trakker.ui.components.ItemType

sealed interface FeedEvent {
    data class focus(val item: Item)                : FeedEvent
    object unfocus                                  : FeedEvent
    data class changeFeedType(val type: ItemType)   : FeedEvent
    data class search(val name: String)             : FeedEvent
    object closeSearch                              : FeedEvent
    object refresh                                  : FeedEvent
    object clearFailure                             : FeedEvent
    object nextPage                                 : FeedEvent
}