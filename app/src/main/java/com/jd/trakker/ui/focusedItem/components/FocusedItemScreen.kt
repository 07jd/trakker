package com.jd.trakker.ui.focusedItem.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.jd.trakker.R
import com.jd.trakker.data.local.item.GameItem
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.item.MovieItem
import com.jd.trakker.data.local.item.ShowItem
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.local.tracked.toStringRes
import com.jd.trakker.ui.components.InteractiveCard
import com.jd.trakker.ui.focusedItem.FocusedItemEvent
import java.util.Locale

@Composable
private fun TrackedDialog(
    selected: TrackedStatus,
    onChange: (FocusedItemEvent) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        modifier = Modifier.fillMaxWidth(.9f),
        properties = DialogProperties()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHighest
        ) {
            Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
            ) {
                TrackedStatus.entries.forEach {
                    val selected = it == selected

                    InteractiveCard(
                        title = stringResource(it.toStringRes()),
                        highlightCard = selected,
                        onCardClick = { onChange(FocusedItemEvent.ChangeState(it)) }
                    )
                }


                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text(
                        text = stringResource(R.string.go_back),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun FocusedItemScreen(
    item: Item,
    trackedStatus: TrackedStatus,
    favorite: Boolean,
    userRating: Double?,
    useVibrations: Boolean = true,
    onEvent: (FocusedItemEvent) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var rateDialogOpen by rememberSaveable { mutableStateOf(false) }
    var trackedDialog by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val topBarAlpha by remember {
        derivedStateOf {
            (scrollState.value / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
        }
    }

    val summary = when(item) {
        is GameItem  -> item.game.summary
        is MovieItem -> item.movie.summary
        is ShowItem  -> item.show.summary
    }

    val genres = when(item) {
        is GameItem  -> item.game.genres.distinct()
        is MovieItem -> item.movie.genres.distinct()
        is ShowItem  -> item.show.genres.distinct()
    }

    val platforms = when(item) {
        is GameItem -> item.game.platformIconRes()
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(
            title = when(item) {
                is GameItem  -> item.game.name
                is MovieItem -> item.movie.name
                is ShowItem  -> item.show.name
            },
            url = when(item) {
                is GameItem  -> item.game.url
                is MovieItem -> item.movie.url
                is ShowItem  -> item.show.url
            },
            alpha = topBarAlpha
        ) {
            if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onEvent(FocusedItemEvent.GoBack)
        } }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Rate dialog
            if(rateDialogOpen) {
                RateDialog(
                    value = userRating,
                    onDismiss = { rateDialogOpen = false }
                ) {
                    if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                    onEvent(FocusedItemEvent.Rate(it))
                }
            }

            // Tracked dialog
            if(trackedDialog) {
                TrackedDialog(
                    selected = trackedStatus,
                    onDismiss = { trackedDialog = !trackedDialog },
                    onChange = { onEvent(it); trackedDialog = !trackedDialog  }
                )
            }

            // Background banners
            BackgroundBanner(item)

            // Main content
            Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 15.dp)
                    .padding(top = 20.dp, bottom = 25.dp),
                   verticalArrangement = Arrangement.Top,
                   horizontalAlignment = Alignment.Start
            ) {
                TopCardInfo(
                    item = item,
                    trackedStatus = trackedStatus,
                    onClick = { trackedDialog = !trackedDialog }
                )

                // Row with platforms and their icons
                if(platforms != null) {
                    Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) { platforms.forEach { IconChip(it.second, it.first) } }
                }

                // Genres chips
                Row(modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    genres.forEach { IconChip(it) }
                }

                Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Button to favorite
                    IconButton(
                        state = favorite,
                        iconRes = R.drawable.heart,
                        activeIconRes = R.drawable.heart_fill,
                        iconDescription = stringResource(R.string.favorite),
                        label = stringResource(R.string.favorite),
                        activeLabel = stringResource(R.string.favorited)
                    ) {
                        if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onEvent(FocusedItemEvent.Favorite)
                    }

                    // Button to give a personal rating
                    IconButton(
                        state = userRating != null,
                        iconRes = R.drawable.star,
                        activeIconRes = R.drawable.star_fill,
                        iconDescription = stringResource(R.string.rate_it),
                        label = stringResource(R.string.rate_it),
                        activeLabel = userRating?.let { "%.2f".format(Locale.US, it) } ?:
                                      stringResource(R.string.rate_it)
                    ) { rateDialogOpen = true }


                    // Button to view notes
                    IconButton(
                        iconRes = R.drawable.archive_fill,
                        iconDescription = stringResource(R.string.notes),
                        label = stringResource(R.string.notes),
                    ) { onEvent(FocusedItemEvent.NotesScreen) }
                }


                CollapsableText(
                    text = summary,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    useVibrations = useVibrations
                )
            }
        }
    }
}