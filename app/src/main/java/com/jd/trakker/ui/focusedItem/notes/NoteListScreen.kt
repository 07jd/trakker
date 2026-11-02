package com.jd.trakker.ui.focusedItem.notes

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.feed.components.SadScreen
import com.jd.trakker.ui.focusedItem.notes.components.NoteDeletionDialog
import com.jd.trakker.ui.focusedItem.notes.components.NoteTitleCard
import com.jd.trakker.ui.focusedItem.notes.components.TopBar

@Composable
fun NoteListScreen(
    notes: List<Pair<String, String>>,
    onBack: () -> Unit,
    onSelect: (Int?) -> Unit,
    onDelete: (Int) -> Unit
) {
    var deletionDialogOpen by rememberSaveable{ mutableStateOf(false) }
    var deletionNoteIndex: Int? by rememberSaveable{ mutableStateOf(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSelect(null) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = stringResource(R.string.add_note),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        topBar = {
            TopBar(
                onBack = { onBack() },
                title = {
                    Text(
                        text = stringResource(R.string.notes),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                    )
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = notes.size
        ) { noteCount ->
            if(noteCount == 0) {
                SadScreen(stringResource(R.string.no_notes))
            } else {
                // Show confirm dialog in case user wants to delete a note
                if(deletionDialogOpen) {
                    val noteTitle = notes[deletionNoteIndex!!].first

                    NoteDeletionDialog(
                        noteTitle = if(noteTitle.isEmpty() || noteTitle.isBlank())
                                    stringResource(R.string.untitled_note, deletionNoteIndex!! + 1)
                                    else noteTitle,
                        onDismiss = { deletionDialogOpen = false; deletionNoteIndex = null },
                        onConfirm = { onDelete(deletionNoteIndex!!); deletionDialogOpen = false; deletionNoteIndex = null }
                    )
                }

                // List notes
                LazyColumn(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(notes) { index, (title, _) ->
                        NoteTitleCard(
                            title,
                            index,
                            onSelect = { onSelect(it) }
                        ) {
                            deletionNoteIndex = it
                            deletionDialogOpen = true
                        }
                    }
                }
            }
        }
    }
}