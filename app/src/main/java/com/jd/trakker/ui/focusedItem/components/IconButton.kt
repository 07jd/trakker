package com.jd.trakker.ui.focusedItem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    state: Boolean = false,
    iconRes: Int,
    iconDescription: String,
    activeIconRes: Int? = null,
    activeIconDescription: String? = null,
    label: String,
    activeLabel: String? = null,
    onClick: () -> Unit
) {
    Column(modifier = Modifier
            .width(75.dp)
            .fillMaxHeight(2/3f)
            .clickable{ onClick() },
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = state,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { active ->
            if(active) {
                Icon(
                    painter = painterResource(activeIconRes ?: iconRes),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = activeIconDescription ?: iconDescription
                )
            } else {
                Icon(
                    painter = painterResource(iconRes),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = iconDescription
                )
            }
        }

        Text(
            text = if(state) activeLabel ?: label else label,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}