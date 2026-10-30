package com.jd.trakker.ui.focusedItem.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.feed.components.SadScreen
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
            NoteEditing(notes, idx, note) { onEvent(FocusedItemEvent.CloseNote(it)) }
        } else {
           NotesViewing(notes, onBack = { onEvent(FocusedItemEvent.NotesScreen) }) {
               onEvent(FocusedItemEvent.OpenNote(it))
           }
        }
    }
}

@Composable
private fun NotesViewing(
    notes: List<Pair<String, String>>,
    onBack: () -> Unit,
    onSelect: (Int?) -> Unit
) {
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
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Back button
                Box(modifier = Modifier
                        .size(46.dp)
                        .padding(end = 12.dp)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_double_left),
                        contentDescription = stringResource(R.string.go_back)
                    )
                }

                // Title
                Text(
                    text = stringResource(R.string.notes),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = notes.size
        ) { noteCount ->
            if(noteCount == 0) {
                SadScreen(stringResource(R.string.no_notes))
            } else {
                LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    itemsIndexed(notes) { index, (title, _) ->
                        // TODO: make this prettier
                        Card(modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                             onClick = { onSelect(index) }
                        ) {
                            Text(text = if(title.isBlank() || title.isEmpty())
                                        stringResource(R.string.untitled_note, index)
                                        else title,
                                 modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteEditing(
    notes: List<Pair<String, String>>,
    index: Int,
    editingNote: Pair<String, String>,
    onBack: (List<Pair<String, String>>) -> Unit
) {
    val titleState = rememberTextFieldState(initialText = editingNote.first)
    val contentState = rememberTextFieldState(initialText = editingNote.second)

    // Save the note on back
    BackHandler(true) {
        val editedNotes = notes.toMutableList()
        editedNotes[index] = titleState.text.toString() to contentState.text.toString()
        onBack(editedNotes)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                // Go back button
                Box(modifier = Modifier
                        .size(46.dp)
                        .clickable {
                            val editedNotes = notes.toMutableList()
                            editedNotes[index] = titleState.text.toString() to contentState.text.toString()
                            onBack(editedNotes)
                        }) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_double_left),
                        contentDescription = stringResource(R.string.go_back)
                    )
                }

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = titleState,
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                modifier = Modifier.fillMaxSize(),
                state = contentState
            )
        }
    }
}