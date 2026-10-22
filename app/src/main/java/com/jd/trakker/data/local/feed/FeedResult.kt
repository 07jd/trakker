package com.jd.trakker.data.local.feed

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FeedType {
    Games,
    Movies,
    Shows
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