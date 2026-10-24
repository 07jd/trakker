package com.jd.trakker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jd.trakker.R

@Composable
private fun ItemCardBackup(item: Item) {
    val itemTitle = when(item) {
        is GameItem  -> item.game.name
        is MovieItem -> item.movie.title
        is ShowItem  -> item.show.name
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = itemTitle,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            softWrap = true
        )
    }
}

@Composable
fun ItemCard(item: Item, onClick: (() -> Unit)? = null) {
    var imageFailedToLoad by remember { mutableStateOf(false) }

    val imageURL = when(item) {
        is GameItem  -> item.game.getCoverURL()
        is MovieItem -> item.movie.getCoverURL()
        is ShowItem  -> item.show.getCoverURL()
    }

    val imageDescription = when(item) {
        is GameItem  -> stringResource(R.string.item_card_image_description_game, item.game.name)
        is MovieItem -> stringResource(R.string.item_card_image_description_movie_or_show, item.movie.title)
        is ShowItem  -> stringResource(R.string.item_card_image_description_movie_or_show, item.show.name)
    }

    Box(
        modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    when(item) {
                        is GameItem -> 3 / 4f
                        else        -> 2 / 3f
                    }
                )
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(true) { onClick?.invoke() },
    ) {
        // Backup Item Card on fail to load image
        if (imageFailedToLoad) {
            ItemCardBackup(item)
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).crossfade(300).data(imageURL)
                    .build(),
            contentDescription = imageDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            onError = { imageFailedToLoad = true })
    }
}
