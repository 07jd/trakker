package com.jd.trakker.ui.focusedItem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jd.trakker.R

@Composable
fun CollapsableText(
    text: String,
    modifier: Modifier,
    useVibrations: Boolean = true,
    hiddenHeight: Dp = 75.dp
) {
    // No content
    if(text.isEmpty()) return

    val haptics = LocalHapticFeedback.current
    val bg = MaterialTheme.colorScheme.background
    var expanded by rememberSaveable{ mutableStateOf(false) }

    Column(modifier = modifier) {
        Box(modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .heightIn(max = if(expanded) Dp.Infinity else hiddenHeight),
        ) {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis
            )

            if(!expanded) {
                Box(
                    modifier = Modifier
                            .matchParentSize()
                            .background(Brush.verticalGradient(colors = listOf(
                                        bg.copy(alpha = 0f),
                                        bg
                            )))
                )
            }
        }


        // Expand 'button'
        Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .width(30.dp)
                    .clickable{
                        if(useVibrations) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        expanded = !expanded
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(expanded) { isExpanded ->
                    if(!isExpanded) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_down),
                            contentDescription = stringResource(R.string.expand_text)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.chevron_double_up),
                            contentDescription = stringResource(R.string.shrink_text)
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.weight(1f))
        }
    }
}