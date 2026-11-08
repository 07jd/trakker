package com.jd.trakker.ui.tracked

import com.jd.trakker.data.local.item.Item
import com.jd.trakker.ui.components.FilterOrder
import com.jd.trakker.ui.components.FilterType

sealed interface TrackedEvent {
    data class ChangeFilter(val order: FilterOrder, val type: FilterType): TrackedEvent
    data class ChangeCollapsedState(val key: String, val value: Boolean) : TrackedEvent
    data class Focus(val item: Item) : TrackedEvent
    data class ViewAll(val name: String): TrackedEvent
    data class Search(val query: String): TrackedEvent
    object Unfocus: TrackedEvent
    object CloseViewAll: TrackedEvent
    object CloseSearch: TrackedEvent
}