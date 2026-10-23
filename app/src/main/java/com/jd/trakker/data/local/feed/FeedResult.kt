package com.jd.trakker.data.local.feed

import com.jd.trakker.R
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FeedType {
    Games,
    Movies,
    Shows
}

@StringRes
fun FeedType.toStringRes(): Int = when(this) {
    FeedType.Games  -> R.string.feed_type_games
    FeedType.Movies -> R.string.feed_type_movies
    FeedType.Shows  -> R.string.feed_type_shows
}

@Entity(tableName = "feedResult")
data class FeedResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: FeedType,
    val page: Int = 1,
    val ids: List<Int> = emptyList()
)