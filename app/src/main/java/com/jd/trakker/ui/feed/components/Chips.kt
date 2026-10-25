package com.jd.trakker.ui.feed.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.ui.components.ItemType
import com.jd.trakker.ui.components.toStringRes

@Composable
fun FeedTypeChips(enabledFeeds: List<ItemType>, feedType: ItemType, onClick: (ItemType) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 5.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        enabledFeeds.forEach {
            FilterChip(
                selected = feedType == it,
                label = { Text(stringResource(it.toStringRes())) },
                onClick = { onClick(it) },
                leadingIcon = if (feedType == it) { {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.check),
                        contentDescription = stringResource(R.string.selected),
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    ) }
            } else { null })
        }
    }
}
