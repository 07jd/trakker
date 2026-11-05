package com.jd.trakker.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InteractiveCard(
    title: String,
    label: String? = null,
    cardHeight: Dp = 72.dp,
    highlightCard: Boolean = false,
    onCardClick: (() -> Unit)? = null,
    rightSideInteraction: (@Composable () -> Unit)? = null
) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .clickable(onCardClick != null) { onCardClick?.invoke() }
            .height(cardHeight)
            .border(
                width = if(highlightCard) 2.dp else 0.dp,
                color = if(highlightCard) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
         shape = RoundedCornerShape(16.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer))
    {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info texts
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                // Label
                if(label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            rightSideInteraction?.invoke()
        }
    }
}