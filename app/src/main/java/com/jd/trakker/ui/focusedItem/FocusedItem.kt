package com.jd.trakker.ui.focusedItem

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jd.trakker.data.local.GameItem
import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.MovieItem
import com.jd.trakker.data.local.ShowItem
import com.jd.trakker.ui.focusedItem.components.BackgroundBanner
import com.jd.trakker.ui.focusedItem.components.CollapsableText
import com.jd.trakker.ui.focusedItem.components.IconChip
import com.jd.trakker.ui.focusedItem.components.TopBar
import com.jd.trakker.ui.focusedItem.components.TopCardInfo

@Composable
fun FocusedItem(innerRootPadding: PaddingValues, item: Item, onBack: () -> Unit) {
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
        is GameItem -> item.game.plataformIconRes()
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(bottom = innerRootPadding.calculateBottomPadding()),
        topBar = { TopBar(
            url = when(item) {
                is GameItem  -> item.game.url!!
                is MovieItem -> "https://www.themoviedb.org/movie/${item.movie.id}"
                is ShowItem  -> "https://www.themoviedb.org/tv/${item.show.id}"
            },
            alpha = topBarAlpha
        ) { onBack() } }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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

                // Row with plaforms and their icons
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

                // TODO: BUTTONS FOR FAVORITE, ADD NOTE ETC...

                CollapsableText(
                    text = summary,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
            }
        }
    }
}