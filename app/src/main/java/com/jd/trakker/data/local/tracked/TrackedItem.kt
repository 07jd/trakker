package com.jd.trakker.data.local.tracked

import androidx.annotation.StringRes
import androidx.room.Entity
import com.jd.trakker.R
import com.jd.trakker.data.local.ItemType

enum class TrackedStatus{
    UNTRACKED,
    PENDING,
    COMPLETED,
    DROPPED
}

@StringRes
fun TrackedStatus.toStringRes(): Int =  when(this) {
    TrackedStatus.UNTRACKED -> R.string.tracked_status_untracked
    TrackedStatus.PENDING   -> R.string.tracked_status_pending
    TrackedStatus.COMPLETED -> R.string.tracked_status_completed
    TrackedStatus.DROPPED   -> R.string.tracked_status_dropped
}

@Entity(
    tableName = "tracked",
    primaryKeys = ["id", "type"]
)
data class TrackedItem(
    val id: Int,
    val type: ItemType,

    val userRating: Double? = null,
    val status: TrackedStatus = TrackedStatus.UNTRACKED,
    val favorite: Boolean = false,
    val notes: List<Pair<String, String>> = emptyList() // Note title, content
)