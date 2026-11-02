package com.jd.trakker.ui.focusedItem.notes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R

@Composable
fun TopBar(
    title: @Composable () -> Unit,
    onBack: () -> Unit
) {
    Row(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        // Back button
        Box(modifier = Modifier
                .weight(.15f)
                .fillMaxHeight()
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.chevron_double_left),
                contentDescription = stringResource(R.string.go_back)
            )
        }

        // Title/Input etc...
        Box(modifier = Modifier.weight(.85f).fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) { title() }
    }
}