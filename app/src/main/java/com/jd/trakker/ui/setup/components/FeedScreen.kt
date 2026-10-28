package com.jd.trakker.ui.setup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.ItemType
import com.jd.trakker.ui.setup.SetupEvent

@Composable
fun FeedScreen(
    selected: List<ItemType>,
    useVibrations: Boolean,
    onEvent: (SetupEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(25.dp),
           verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.setup_feed_types),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.setup_feed_types_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Feed types
        FeedTypeCards(
            currentSelected = selected,
            useVibrations = useVibrations
        ) { type, state -> onEvent(SetupEvent.ChangeFeedTypes(type, state)) }
    }
}