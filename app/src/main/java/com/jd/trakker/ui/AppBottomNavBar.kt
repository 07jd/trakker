package com.jd.trakker.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomNavBar(
    currentRoute: Destination,
    navigateTo: (Destination) -> Unit
) {
    NavigationBar {
        Destination.entries.forEach { destination ->
            val isSelected = (currentRoute.ordinal == destination.ordinal)

            NavigationBarItem(
                selected = isSelected,
                onClick = { navigateTo(destination) },
                icon = {
                    AnimatedContent(targetState = isSelected) { active ->
                        if (active) {
                            Icon(
                                imageVector = ImageVector.vectorResource(destination.activeIcon),
                                modifier = Modifier.size(20.dp),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                imageVector = ImageVector.vectorResource(destination.inactiveIcon),
                                modifier = Modifier.size(20.dp),
                                contentDescription = null
                            )
                        }
                    }
                },
                label = { Text(text = stringResource(destination.stringResLabel)) }
            )
        }
    }
}