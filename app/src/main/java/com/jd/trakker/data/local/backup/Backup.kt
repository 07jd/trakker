package com.jd.trakker.data.local.backup

import com.jd.trakker.data.local.item.GameItem
import com.jd.trakker.data.local.item.MovieItem
import com.jd.trakker.data.local.item.ShowItem
import com.jd.trakker.data.local.tracked.TrackedItem

// Json backup format
data class Backup(
    val timestamp: Long = System.currentTimeMillis(),
    val games: List<GameItem>,
    val movies: List<MovieItem>,
    val shows: List<ShowItem>,
    val trackedItems: List<TrackedItem>
)