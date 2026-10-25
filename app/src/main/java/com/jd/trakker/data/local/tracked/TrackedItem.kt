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

    val userRating: Double,
    val status: TrackedStatus,
    val notes: List<String>
)