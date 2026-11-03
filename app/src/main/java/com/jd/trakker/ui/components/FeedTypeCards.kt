package com.jd.trakker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.ItemType
import com.jd.trakker.data.local.toStringRes

@Composable
fun FeedTypeCards(
    currentSelected: List<ItemType>,
    useVibrations: Boolean = true,
    onClick: (ItemType, Boolean) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    // FIX to edge case: when user clicks all 3 at the same time
    LaunchedEffect(currentSelected) { if (currentSelected.isEmpty()) onClick(ItemType.Game, true) }

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ItemType.entries.forEach { item ->
            val selected = currentSelected.contains(item)
            val canClick = !(selected && currentSelected.size <= 1)

            Card(modifier = Modifier
                    .weight(1f)
                    .clickable(canClick) {
                        if (useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onClick(item, !selected)
                    },
                 shape = RoundedCornerShape(16.dp),
                 border = if(selected) { BorderStroke(2.dp, MaterialTheme.colorScheme.primary) } else null
            ) {
                Column(modifier = Modifier.padding(8.dp),
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(125.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconRes = when(item) {
                            ItemType.Game  -> R.drawable.controller
                            ItemType.Movie -> R.drawable.film
                            ItemType.Show  -> R.drawable.collection
                        }

                        Icon(
                            painter = painterResource(iconRes),
                            modifier = Modifier.fillMaxSize().padding(6.dp),
                            contentDescription = stringResource(item.toStringRes())
                        )
                    }

                    // Item name
                    Text(
                        text = stringResource(item.toStringRes()),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )

                    // Selected checkbox
                    Box(modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Text(
                                text = stringResource(R.string.checkmark),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}