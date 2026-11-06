package com.jd.trakker.ui.focusedItem

import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.tracked.TrackedItem

data class FocusedItemState(
    val item: Item,
    val trackedItem: TrackedItem? = null,
    val loading: Boolean = true,
    val onNotesScreen: Boolean = false,
    val note: Pair<String, String>? = null,
    val noteIndex: Int? = null
)