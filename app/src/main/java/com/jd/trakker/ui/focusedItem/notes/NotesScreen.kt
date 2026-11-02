package com.jd.trakker.ui.focusedItem.notes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import com.jd.trakker.ui.focusedItem.FocusedItemEvent

@Composable
fun NotesScreen(
    notes: List<Pair<String, String>>,
    index: Int?,
    selectedNote: Pair<String, String>?,
    onEvent: (FocusedItemEvent) -> Unit
) {
    // Close this screen on back
    BackHandler(true) { onEvent(FocusedItemEvent.NotesScreen) }

    AnimatedContent(
        targetState = index to selectedNote
    ) { (idx, note) ->
        if(idx != null && note != null) {
            NoteEditingScreen(
                notes       = notes,
                index       = idx,
                editingNote = note,
                onBack      = { onEvent(FocusedItemEvent.CloseNote(it)) }
            )
        } else {
           NoteListScreen(
               notes    = notes,
               onBack   = { onEvent(FocusedItemEvent.NotesScreen) },
               onSelect = { onEvent(FocusedItemEvent.OpenNote(it)) },
               onDelete = { onEvent(FocusedItemEvent.CloseNote(notes.filterIndexed { index, _ -> index != it })) }
           )
        }
    }
}