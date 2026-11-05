package com.jd.trakker.ui.focusedItem

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.ui.focusedItem.components.FocusedItemScreen
import com.jd.trakker.ui.focusedItem.notes.NotesScreen

@Composable
fun FocusedItem(
    innerRootPadding: PaddingValues,
    uiState: FocusedItemState,
    useVibrations: Boolean = true,
    onEvent: (FocusedItemEvent) -> Unit
) {
    // Go back to previous screen
    BackHandler(!uiState.onNotesScreen) { onEvent(FocusedItemEvent.GoBack) }

    AnimatedContent(uiState.onNotesScreen) { showNoteList ->
        if(showNoteList) {
            Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerRootPadding)
            ) {
                NotesScreen(
                    notes = uiState.trackedItem?.notes ?: emptyList(),
                    index = uiState.noteIndex,
                    selectedNote = uiState.note,
                    useVibrations = useVibrations
                ) { onEvent(it) }
            }
        } else {
            Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerRootPadding.calculateBottomPadding())
            ) {
                FocusedItemScreen(
                    item = uiState.item,
                    trackedStatus = uiState.trackedItem?.status ?: TrackedStatus.UNTRACKED,
                    favorite = uiState.trackedItem?.favorite ?: false,
                    userRating = uiState.trackedItem?.userRating,
                    useVibrations = useVibrations
                ) { onEvent(it) }
            }
        }
    }
}