package com.jd.trakker.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jd.trakker.ui.components.GameItem
import com.jd.trakker.ui.components.Item
import com.jd.trakker.ui.components.MovieItem
import com.jd.trakker.ui.components.ShowItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun ItemGrid(
    state: LazyGridState,
    items: List<Item>,
    onClick: (Item) -> Unit,
    onLoadMore: (() -> Unit)? = null,
    // When reaches last_element - bottomOffset, invokes onReachedBottom()
    bottomOffset: Int = 5,
) {
    // Display cover art
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 8.dp),
        state = state,
        columns = GridCells.Adaptive(minSize = 150.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = items,
            key = {
                when (it) {
                    is GameItem  -> "game_${it.game.id}"
                    is MovieItem -> "movie_${it.movie.id}"
                    is ShowItem  -> "show_${it.show.id}"
                }
            }
        ) {
            ItemCard(it) { onClick(it) }
        }
    }

    // Trigger onReachedBottom() if possible
    LaunchedEffect(state) {
        snapshotFlow {
            val totalItems = state.layoutInfo.totalItemsCount - 1
            val lastItem = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastItem + bottomOffset >= totalItems
        }.distinctUntilChanged().filter { it }.collect { onLoadMore?.invoke() }
    }
}