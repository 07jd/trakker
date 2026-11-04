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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.GameItem
import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.MovieItem
import com.jd.trakker.data.local.ShowItem
import com.jd.trakker.ui.focusedItem.FocusedItemEvent
import java.util.Locale

@Composable
fun FocusedItemScreen(
    item: Item,
    favorite: Boolean,
    userRating: Double?,
    useVibrations: Boolean = true,
    onEvent: (FocusedItemEvent) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var rateDialogOpen by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val topBarAlpha by remember {
        derivedStateOf {
            (scrollState.value / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
        }
    }

    val summary = when(item) {
        is GameItem  -> item.game.summary ?: ""
        is MovieItem -> item.movie.overview
        is ShowItem  -> item.show.overview
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
                is MovieItem -> item.movie.title
                is ShowItem  -> item.show.name
            },
            url = when(item) {
                is GameItem  -> item.game.url!!
                is MovieItem -> "https://www.themoviedb.org/movie/${item.movie.id}"
                is ShowItem  -> "https://www.themoviedb.org/tv/${item.show.id}"
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
                    onDismiss = { rateDialogOpen = false }
                ) {
                    if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                    onEvent(FocusedItemEvent.Rate(it))
                }
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
                TopCardInfo(item)

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

                // TODO: EXPORT BUTTON
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