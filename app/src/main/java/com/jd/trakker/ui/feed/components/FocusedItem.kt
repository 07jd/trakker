package com.jd.trakker.ui.feed.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jd.trakker.ui.feed.FeedItem

// TODO
@Composable
fun FocusedItem(item: FeedItem, onBack: () -> Unit) {
    Text("You focused an item!")
    Button(onClick = { onBack() }) {
        Text("Click here to go back")
    }
}