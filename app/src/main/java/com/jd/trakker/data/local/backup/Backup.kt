package com.jd.trakker.data.local.backup

import com.jd.trakker.data.local.GameItem
import com.jd.trakker.data.local.MovieItem
import com.jd.trakker.data.local.ShowItem
import com.jd.trakker.data.local.tracked.TrackedItem

// Json backup format
data class Backup(
    val timestamp: Long = System.currentTimeMillis(),
    val games: List<GameItem>,
    val movies: List<MovieItem>,
    val shows: List<ShowItem>,
    val trackedItems: List<TrackedItem>
)