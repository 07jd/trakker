package com.jd.trakker.ui.tracked

import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.tracked.TrackedStatus

// Maps [ENUM.NAME] = VALUE
data class Stats(
    // Mean of the ratings per type, Ex.: [ItemType.Game.name] -> 7.32
    val meanUserRating  : Map<ItemType, Double>     = emptyMap(),
    val meanRating      : Map<ItemType, Double>     = emptyMap(),
    // Count of items of each type
    val count           : Map<ItemType, Int>        = emptyMap(),
    // Count of each genre
    val genresCount     : Map<String, Int>          = emptyMap(),
    // Count of categories (Pending, Completed, ....)
    val categoryCount   : Map<TrackedStatus, Int>   = emptyMap()
)