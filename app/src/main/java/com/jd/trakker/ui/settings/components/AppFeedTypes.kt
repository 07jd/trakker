package com.jd.trakker.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.ui.components.FeedTypeCards
import com.jd.trakker.ui.settings.SettingsEvent

@Composable
fun AppFeedTypes(
    gameFeed: Boolean,
    movieFeed: Boolean,
    showFeed: Boolean,
    horizontalPadding: Dp = 12.dp,
    useVibrations: Boolean = true,
    onEvent: (SettingsEvent) -> Unit
) {
    val enabledFeeds = buildList {
        if (gameFeed)   add(ItemType.Game)
        if (movieFeed)  add(ItemType.Movie)
        if (showFeed)   add(ItemType.Show)
    }

    Column(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        // Label
        Text(
            text = stringResource(R.string.setup_feed_types),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FeedTypeCards(
            currentSelected = enabledFeeds,
            useVibrations = useVibrations,
            onClick = { type, state -> onEvent(SettingsEvent.ChangeFeedType(type, state)) }
        )
    }
}