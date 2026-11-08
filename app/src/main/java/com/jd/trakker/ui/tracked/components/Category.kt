package com.jd.trakker.ui.tracked.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.ui.components.ItemCard

@Composable
fun Category(
    title: String,
    items: List<Item>,
    state: LazyListState = rememberLazyListState(),
    collapsed: Boolean,
    height: Dp = 230.dp,
    onCollapse: () -> Unit,
    onViewAllClick: () -> Unit,
    onItemClick: (Item) -> Unit,
) {
    val bg = MaterialTheme.colorScheme.background
    val startAlpha by remember {
        derivedStateOf {
            if(!state.canScrollBackward) 0f else 1f
        }
    }

    val endAlpha by remember {
        derivedStateOf {
            if(!state.canScrollForward) 0f else 1f
        }
    }

    Column(
        modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = height)
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable { onCollapse() },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.padding(end = 4.dp)) {
                    AnimatedContent(
                        targetState = collapsed
                    ) { isCollapsed ->
                        if(isCollapsed) {
                            Icon(
                                painter = painterResource(R.drawable.chevron_down),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.chevron_up),
                                contentDescription = null
                            )
                        }
                    }
                }


                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.weight(1f))
            }


            TextButton(
                onClick = { onViewAllClick() }
            ) {
                Text(
                    text = stringResource(R.string.view_all),
                    textAlign = TextAlign.Center,
                )
            }
        }

        AnimatedVisibility(
            visible = !collapsed,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Nothing to list
                if(items.isEmpty()) {
                    Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(3/4f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.empty),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }


                    return@Box
                }

                // List items
                LazyRow(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                ) {
                    items(items) {
                        ItemCard(item = it, onClick = { onItemClick(it) })
                    }
                }

                // Gradient
                Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colorStops = arrayOf(
                                        0f to bg.copy(alpha = startAlpha),
                                        0.05f to bg.copy(alpha = 0f),
                                        0.95f to bg.copy(alpha = 0f),
                                        1f to bg.copy(alpha = endAlpha)
                                    )
                                )
                            )
                )
            }
        }
    }
}