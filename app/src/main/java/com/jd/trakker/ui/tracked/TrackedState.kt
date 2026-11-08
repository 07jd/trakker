package com.jd.trakker.ui.tracked

import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.tracked.TrackedItem
import com.jd.trakker.ui.components.FilterDefaults
import com.jd.trakker.ui.components.FilterOrder
import com.jd.trakker.ui.components.FilterType

data class TrackedState(
    val loading: Boolean            = true,

    // Data
    val pendingItems: List<Item>    = emptyList(),
    val completedItems: List<Item>  = emptyList(),
    val droppedItems: List<Item>    = emptyList(),
    val favoriteItems: List<Item>   = emptyList(),
    val searchResults: List<Item>?  = null,

    // Filter settings
    val filterOrder: FilterOrder                    = FilterDefaults.order,
    val filterType: FilterType                      = FilterDefaults.type,
    val collapsedCategories: Map<String, Boolean>   = emptyMap(),
    val viewAllType: String?                        = null,

    // Focused item
    val focusedItem: Item? = null,
    val focusedItemTracked: TrackedItem? = null,

    // Stats
    val stats: Stats = Stats()
)