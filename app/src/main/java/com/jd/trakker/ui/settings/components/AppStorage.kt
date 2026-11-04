package com.jd.trakker.ui.settings.components

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.imageLoader
import com.jd.trakker.R
import com.jd.trakker.ui.components.InteractiveCard
import com.jd.trakker.ui.settings.SettingsEvent

enum class Operation {
    Backup,
    Restore
}

@StringRes
fun Operation.toStringRes(): Int = when(this) {
    Operation.Backup  -> R.string.backup
    Operation.Restore -> R.string.restore
}

const val KILOBYTES_IN_BYTES    = 1024L
const val MEGABYTE_IN_BYTES     = KILOBYTES_IN_BYTES * 1024L
const val GIGABYTE_IN_BYTES     = MEGABYTE_IN_BYTES * 1024L
private fun bytesToString(bytes: Long): String {
    return when {
        bytes < KILOBYTES_IN_BYTES                          -> "$bytes B"
        bytes in KILOBYTES_IN_BYTES..<MEGABYTE_IN_BYTES  -> "${(bytes/KILOBYTES_IN_BYTES).toInt()} KB"
        bytes < GIGABYTE_IN_BYTES                           -> "${(bytes/MEGABYTE_IN_BYTES).toInt()} MB"
        else                                                -> "${(bytes/GIGABYTE_IN_BYTES).toInt()} GB"
    }
}

// TODO: Make this prettier
@Composable
private fun FeedBackDialog(
    title: String,
    feedBackMessageRes: Int?,
    canDismiss: Boolean,
    onDismiss: () -> Unit
) {
    BackHandler(true) { if(canDismiss) onDismiss() }

    BasicAlertDialog(
        properties = DialogProperties(),
        onDismissRequest = { if(canDismiss) onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title)

                if(feedBackMessageRes == null) {
                    LinearWavyProgressIndicator()
                } else {
                    Text(text = stringResource(feedBackMessageRes))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        enabled = canDismiss,
                        onClick = { onDismiss() }
                    ) {
                        Text(stringResource(R.string.go_back))
                    }
                }
            }
        }
    }
}

@Composable
fun AppStorage(
    horizontalPadding: Dp = 12.dp,
    feedBackMessageRes: Int?,
    useVibrations: Boolean = true,
    onEvent: (SettingsEvent) -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var waitingForFeedback by rememberSaveable{ mutableStateOf(false) }
    var operation          by rememberSaveable{ mutableStateOf(Operation.Restore) }

    fun getDiskCacheSizeString(): String {
        val sizeInBytes = context.imageLoader.diskCache?.size
        return if(sizeInBytes != null) {
            bytesToString(sizeInBytes)
        } else {
            "0 KB"
        }
    }
    var diskCacheSize by remember { mutableStateOf(getDiskCacheSizeString()) }

    // Intent handlers
    val pickFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let {
        waitingForFeedback = true
        operation = Operation.Restore

        val rawData = context.contentResolver.openInputStream(it)?.use { inStream ->
            inStream.readBytes().decodeToString()
        }

        onEvent(SettingsEvent.Restore(rawData))
    } }

    val createFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let {
        waitingForFeedback = true
        operation = Operation.Backup

        val outStream = context.contentResolver.openOutputStream(it)
        onEvent(SettingsEvent.Backup(outStream))
    } }

    // When feedback received mark as false
    LaunchedEffect(feedBackMessageRes) {
        if(feedBackMessageRes != null) waitingForFeedback = false
    }

    if(feedBackMessageRes != null || waitingForFeedback) {
        FeedBackDialog(
            title = stringResource(operation.toStringRes()),
            feedBackMessageRes = feedBackMessageRes,
            canDismiss = !waitingForFeedback,
            onDismiss = {
                if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                onEvent(SettingsEvent.ClearFeedbackMessage)
            }
        )
    }

    Column(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        // Label
        Text(
            text = stringResource(R.string.storage),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )

        // Show cache disk space and btn to clear
        InteractiveCard(
            title = stringResource(R.string.image_cache),
            rightSideInteraction = {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Size
                    Text(
                        text = diskCacheSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    Button(
                        onClick = {
                            if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            context.imageLoader.diskCache?.clear()
                            diskCacheSize = getDiskCacheSizeString()
                        }
                    ) {
                        Text(stringResource(R.string.clear))
                    }
                }
            }
        )

        Operation.entries.forEach {
            InteractiveCard(
                title = stringResource(it.toStringRes()),
                label = stringResource(when(it) {
                        Operation.Backup  -> R.string.backup_label
                        Operation.Restore -> R.string.restore_label
                    }
                ),
                onCardClick = {
                    if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    try {
                        when(it) {
                            Operation.Backup  -> createFile.launch("backup")
                            Operation.Restore -> pickFile.launch((arrayOf("text/plain", "application/json")))
                        }
                    } catch(e: Exception) {
                        println("Failed to create the intent of backup/restore!")
                        e.printStackTrace()
                    }
                }
            )
        }
    }
}