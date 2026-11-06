package com.jd.trakker.data.local.tracked

import androidx.annotation.StringRes
import com.jd.trakker.R

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