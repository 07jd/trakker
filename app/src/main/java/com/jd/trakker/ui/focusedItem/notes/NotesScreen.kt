package com.jd.trakker.ui.focusedItem.notes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.jd.trakker.ui.focusedItem.FocusedItemEvent

@Composable
fun NotesScreen(
    notes: List<Pair<String, String>>,
    index: Int?,
    selectedNote: Pair<String, String>?,
    useVibrations: Boolean = true,
    onEvent: (FocusedItemEvent) -> Unit
) {
    // Close this screen on back
    BackHandler(true) { onEvent(FocusedItemEvent.NotesScreen) }
    val haptics = LocalHapticFeedback.current

    AnimatedContent(
        targetState = index to selectedNote
    ) { (idx, note) ->
        if(idx != null && note != null) {
            NoteEditingScreen(
                notes           = notes,
                index           = idx,
                editingNote     = note,
                onBack          = {
                    if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEvent(FocusedItemEvent.CloseNote(it))
                }
            )
        } else {
           NoteListScreen(
               notes    = notes,
               onBack   = {
                   if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                   onEvent(FocusedItemEvent.NotesScreen)
               },
               onSelect = {
                   if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                   onEvent(FocusedItemEvent.OpenNote(it))
               },
               onDelete = {
                   if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                   onEvent(FocusedItemEvent.CloseNote(notes.filterIndexed { index, _ -> index != it }))
               }
           )
        }
    }
}