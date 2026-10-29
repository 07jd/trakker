package com.jd.trakker.ui.focusedItem

import com.jd.trakker.data.local.tracked.TrackedStatus

sealed interface FocusedItemEvent {
    object Favorite                                             : FocusedItemEvent
    object NotesScreen                                          : FocusedItemEvent
    object GoBack                                               : FocusedItemEvent
    data class OpenNote(val num: Int)                           : FocusedItemEvent
    data class CloseNote(val data: List<Pair<String, String>>)  : FocusedItemEvent
    data class Rate(val rating: Double?)                        : FocusedItemEvent
    data class ChangeState(val state: TrackedStatus)            : FocusedItemEvent
}