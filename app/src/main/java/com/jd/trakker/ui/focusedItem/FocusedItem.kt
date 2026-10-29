package com.jd.trakker.ui.focusedItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jd.trakker.data.local.Item
import com.jd.trakker.ui.focusedItem.components.BackgroundBanner
import com.jd.trakker.ui.focusedItem.components.TopBar
import com.jd.trakker.ui.focusedItem.components.TopCardInfo

@Composable
fun FocusedItem(
    item: Item,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(onBack) { println("HEART CLICKED") } }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            // Background banners
            BackgroundBanner(item)

            // Main content
            Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 15.dp)
                        .padding(top = 20.dp),
                   verticalArrangement = Arrangement.Top,
                   horizontalAlignment = Alignment.Start
            ) {
                TopCardInfo(item)
            }
        }
    }
}