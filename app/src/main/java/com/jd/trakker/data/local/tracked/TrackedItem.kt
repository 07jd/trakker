package com.jd.trakker.data.local.tracked

import androidx.room.Entity
import com.jd.trakker.data.local.ItemType

enum class TrackedStatus{
    PENDING,
    COMPLETED,
    DROPPED
}

@Entity(
    tableName = "tracked",
    primaryKeys = ["id", "type"]
)
data class TrackedItem(
    val id: Int,
    val type: ItemType,

    val userRating: Double? = null,
    val status: TrackedStatus? = null,
    val favorite: Boolean = false,
    val notes: List<Pair<String, String>> = emptyList() // Note title, content
)