package com.jd.trakker.ui.focusedItem.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jd.trakker.R
import com.jd.trakker.ui.focusedItem.notes.components.TopBar

@Composable
fun NoteEditingScreen(
    notes: List<Pair<String, String>>,
    index: Int,
    editingNote: Pair<String, String>,
    onBack: (List<Pair<String, String>>) -> Unit
) {
    val titleState = rememberTextFieldState(initialText = editingNote.first)
    val contentState = rememberTextFieldState(initialText = editingNote.second)

    fun saveNoteAndExit() {
        val editedNotes = notes.toMutableList()
        editedNotes[index] = titleState.text.toString() to contentState.text.toString()
        onBack(editedNotes)
    }

    // Save the note on back
    BackHandler(true) {
        saveNoteAndExit()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                onBack = { saveNoteAndExit() },
                title = {
                    Text(
                        text = stringResource(R.string.editing_note),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )

            Spacer(Modifier.height(12.dp))

            BasicTextField(
                state = titleState,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(
                    MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(20.dp))

            androidx.compose.material3.HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.content),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )

            Spacer(Modifier.height(12.dp))

            BasicTextField(
                state = contentState,
                modifier = Modifier
                        .fillMaxSize(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 28.sp
                ),
                lineLimits = TextFieldLineLimits.MultiLine(),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(
                    MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
