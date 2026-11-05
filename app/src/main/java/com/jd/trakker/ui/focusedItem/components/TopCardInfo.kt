package com.jd.trakker.ui.focusedItem.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.GameItem
import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.MovieItem
import com.jd.trakker.data.local.ShowItem
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.local.tracked.toStringRes
import com.jd.trakker.ui.components.ItemCard
import java.util.Locale

@Composable
private fun SideText(
    label: String? = null,
    content: String,
    icon: Pair<Painter, String>? = null,
    labelStyle: TextStyle = MaterialTheme.typography.titleMediumEmphasized,
    contentStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        if(icon != null) {
            Icon(
                painter = icon.first,
                contentDescription = icon.second,
                modifier = Modifier.padding(end = 6.dp),
            )
        }

        if(label != null) {
            Text(
                text = label,
                textAlign = TextAlign.Start,
                maxLines = 1,
                softWrap = false,
                style = labelStyle,
                overflow = TextOverflow.Clip
            )
        }


        Text(
            modifier = Modifier.basicMarquee(Int.MAX_VALUE),
            text = content,
            textAlign = TextAlign.Start,
            maxLines = 1,
            softWrap = false,
            style = contentStyle,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun StatusTag(
    trackedStatus: TrackedStatus,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = { onClick() },
        label = { Text(text = stringResource(trackedStatus.toStringRes())) },
        leadingIcon = {
            val iconRes = when(trackedStatus) {
                TrackedStatus.UNTRACKED -> R.drawable.x_circle_fill
                TrackedStatus.PENDING   -> R.drawable.pause_circle_fill
                TrackedStatus.COMPLETED -> R.drawable.check_circle_fill
                TrackedStatus.DROPPED   -> R.drawable.stop_circle_fill
            }

            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        }
    )
}

@Composable
fun TopCardInfo(
    item: Item,
    trackedStatus: TrackedStatus,
    onClick: () -> Unit,
    itemCardWidth: Dp = 140.dp
) {
    val title = when(item) {
        is GameItem  -> item.game.name
        is MovieItem -> item.movie.title
        is ShowItem  -> item.show.name
    }

    // Required by the API's
    val metadataSource = when(item) {
        is GameItem -> stringResource(R.string.provider_igdb)
        else        -> stringResource(R.string.provider_tmdb)
    }

    // Rating 0-10, shown in %.2f
    val rating = when(item) {
        is GameItem  -> item.game.rating?.div(10.0)
        is MovieItem -> item.movie.vote_average
        is ShowItem  -> item.show.vote_average
    }

    Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Cover art
        Box(modifier = Modifier
                .width(itemCardWidth)
                .padding(end = 15.dp)
                .clip(RoundedCornerShape(6.dp))
        ) { ItemCard(item) }

        // Information on the side
        Column(modifier = Modifier.fillMaxSize(),
               verticalArrangement = Arrangement.Bottom
        ) {
            // Item title
            SideText(content = title, contentStyle = MaterialTheme.typography.headlineSmallEmphasized)

            Spacer(modifier = Modifier.height(20.dp))

            // Rating
            SideText(
                icon = Pair(
                    painterResource(R.drawable.star_fill),
                    stringResource(R.string.rating)
                ),
                content = "%.2f".format(Locale.US, rating)
            )

            // Item Release date
            SideText(
                icon = Pair(
                    painterResource(R.drawable.calendar_date_fill), when(item) {
                        is ShowItem -> stringResource(R.string.airdate)
                        else        -> stringResource(R.string.releasedate)
                    }
                ), content = when(item) {
                    is GameItem  -> item.game.localDateString()
                    is MovieItem -> item.movie.localDateString(stringResource(R.string.unknown))
                    is ShowItem  -> item.show.localDateString(stringResource(R.string.unknown))
                }
            )

            // Metadata provider
            SideText(
                icon = Pair(
                    painterResource(R.drawable.info_circle_fill),
                    stringResource(R.string.focused_item_metadata_provider)
                ),
                content = metadataSource
            )

            // Tracked status info
            StatusTag(
                trackedStatus = trackedStatus,
                onClick = { onClick() }
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
