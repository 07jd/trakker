package com.jd.trakker.ui.settings.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

@Composable
fun AppStorage(
    horizontalPadding: Dp = 12.dp,
    onEvent: (SettingsEvent) -> Unit
) {
    val context = LocalContext.current
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
    ) { uri -> uri?.let { TODO() } } // TODO: READ and return the string to the viewModel
    val createFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { TODO() } } // TODO: Use the viewModel backup fn

    // TODO: Error dialog when fails
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

        // TODO: Clear image cache, show how much used and btn to clear
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
                        onClick = { context.imageLoader.diskCache?.clear(); diskCacheSize = getDiskCacheSizeString() }
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