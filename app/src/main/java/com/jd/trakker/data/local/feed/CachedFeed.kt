package com.jd.trakker.data.local.feed

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jd.trakker.data.local.ItemType

@Entity(tableName = "cachedFeed")
data class CachedFeed(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: ItemType,
    val page: Int = 1,
    val ids: List<Int> = emptyList()
)