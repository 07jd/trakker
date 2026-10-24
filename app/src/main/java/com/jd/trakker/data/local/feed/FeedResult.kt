package com.jd.trakker.data.local.feed

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jd.trakker.ui.components.ItemType

@Entity(tableName = "feedResult")
data class FeedResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: ItemType,
    val page: Int = 1,
    val ids: List<Int> = emptyList()
)