package com.jd.trakker.ui.focusedItem.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jd.trakker.R
import com.jd.trakker.data.local.GameItem
import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.MovieItem
import com.jd.trakker.data.local.ShowItem
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun BackgroundBanner(
    item: Item,
    height: Dp = 275.dp,
    blur: Dp = 2.dp,
    timeoutSeconds: Int = 10
) {
    val banners = when(item) {
        is GameItem  -> { item.game.artworks?.map { item.game.getBannerURL(it) } ?: emptyList() }
        is MovieItem -> { listOf(item.movie.getBannerURL()) }
        is ShowItem  -> { listOf(item.show.getBannerURL())  }
    }.filterNotNull()
    val pagerState = rememberPagerState{ banners.size }
    val bg = MaterialTheme.colorScheme.background
    val bottomFade = Brush.verticalGradient(colors = listOf(
        bg.copy(alpha = 0f),
        bg
    ))

    Box(modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .blur(blur)
            .background(bg)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { imgIndex ->
            // Apply gradient from bellow to the top
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = banners[imgIndex],
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = stringResource(R.string.banner),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )

                Box(modifier = Modifier.fillMaxSize().background(bottomFade))
            }
        }
    }

    // Loop through artworks
    LaunchedEffect(Unit) {
        while(true) {
            delay(timeoutSeconds.seconds)
            if(!banners.isEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % (banners.size)
                pagerState.animateScrollToPage(nextPage, animationSpec = tween(750))
            }
        }
    }
}