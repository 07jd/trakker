package com.jd.trakker.data.local.tracked

import androidx.room.Entity
import com.jd.trakker.data.local.item.ItemType

@Entity(
    tableName = "Tracked",
    primaryKeys = ["id", "type"]
)
data class TrackedItem(
    val id: Int,
    val type: ItemType,

    val status: TrackedStatus = TrackedStatus.UNTRACKED,
    val rating: Double? = null,
    val favorite: Boolean = false,
    val notes: List<Pair<String, String>> = emptyList() // (Note title, Content)
)