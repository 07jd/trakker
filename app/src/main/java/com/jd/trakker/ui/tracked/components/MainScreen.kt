package com.jd.trakker.ui.tracked.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.local.tracked.toStringRes
import com.jd.trakker.ui.tracked.TrackedEvent

@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    collapsedCategories: Map<String, Boolean>,
    favoriteItems: List<Item>,
    pendingItems: List<Item>,
    completedItems: List<Item>,
    droppedItems: List<Item>,
    onEvent: (TrackedEvent) -> Unit
) {
    Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // TODO: Stats

        val favoriteTypeName = "favorite"
        val isFavoriteCollapsed = collapsedCategories.getOrDefault(favoriteTypeName, false)
        Category(
            title = stringResource(R.string.favorite_plural),
            items = favoriteItems,
            onItemClick = { onEvent(TrackedEvent.Focus(it)) },
            onViewAllClick = { onEvent(TrackedEvent.ViewAll(favoriteTypeName)) },
            collapsed = isFavoriteCollapsed,
            onCollapse = { onEvent(TrackedEvent.ChangeCollapsedState(favoriteTypeName, !isFavoriteCollapsed)) }
        )


        TrackedStatus.entries.filter { it != TrackedStatus.UNTRACKED }.forEach { category ->
            val isCollapsed = collapsedCategories.getOrDefault(category.name, false)
            Category(
                title = stringResource(category.toStringRes()),
                items = when(category) {
                    TrackedStatus.PENDING   -> pendingItems
                    TrackedStatus.COMPLETED -> completedItems
                    else                    -> droppedItems
                },
                onViewAllClick = { onEvent(TrackedEvent.ViewAll(category.name)) },
                onItemClick = { onEvent(TrackedEvent.Focus(it)) },
                collapsed = isCollapsed,
                onCollapse = { onEvent(TrackedEvent.ChangeCollapsedState(category.name, !isCollapsed))}
            )
        }
    }
}