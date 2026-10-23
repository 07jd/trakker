package com.jd.trakker.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.jd.trakker.R
import com.jd.trakker.ui.feed.FeedGame
import com.jd.trakker.ui.feed.FeedItem
import com.jd.trakker.ui.feed.FeedMovie
import com.jd.trakker.ui.feed.FeedShow

// TODO: In case of load image error besides displaying a icon also display the name of the item
@Composable
fun ItemCard(item: FeedItem, onClick: (() -> Unit)? = null) {
    val imageDescription = when(item) {
        is FeedGame -> stringResource(
            R.string.item_card_image_description_game,
            item.game.name
        )
        is FeedMovie -> stringResource(
            R.string.item_card_image_description_movie_or_show,
            item.movie.title
        )
        is FeedShow -> stringResource(
            R.string.item_card_image_description_movie_or_show,
            item.show.name
        )
    }

    Box(
        modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    when(item) {
                        is FeedGame -> 3 / 4f
                        else        -> 2 / 3f
                    }
                )
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(true) { onClick?.invoke() },
    ) {
        AsyncImage(
            model = when(item) {
                is FeedGame  -> item.game.getCoverURL()
                is FeedMovie -> item.movie.getCoverURL()
                is FeedShow  -> item.show.getCoverURL()
            },
            contentDescription = imageDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            error = if (item is FeedGame) painterResource(id = R.drawable.controller)
            else painterResource(id = R.drawable.film)
        )
    }
}
