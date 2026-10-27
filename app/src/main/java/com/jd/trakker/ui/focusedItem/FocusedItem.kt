package com.jd.trakker.ui.focusedItem

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jd.trakker.R
import com.jd.trakker.data.local.GameItem
import com.jd.trakker.data.local.Item
import com.jd.trakker.ui.components.ItemCard
import com.jd.trakker.data.local.MovieItem
import com.jd.trakker.data.local.ShowItem
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun TopBar(goBack: () -> Unit, onFavorite: () -> Unit) {
    val bg = MaterialTheme.colorScheme.background

    Row(
        modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(bg, bg.copy(alpha = 0f))))
                .statusBarsPadding()
                .padding(horizontal = 30.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.chevron_double_left),
            contentDescription = stringResource(R.string.go_back),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                    .size(22.dp)
                    .clickable { goBack() })


        Icon(
            painter = painterResource(R.drawable.heart),
            contentDescription = stringResource(R.string.add_to_tracked_media),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                    .size(22.dp)
                    .clickable { onFavorite() }
        )
    }
}

@Composable
private fun ShowBanner(item: Item) {
    val nextBannerTimeout = 10_000
    val banners = when(item) {
        is GameItem  -> { item.game.artworks?.map { item.game.getBannerURL(it) } ?: emptyList() }
        is MovieItem -> { listOf(item.movie.getBannerURL()) }
        is ShowItem  -> { listOf(item.show.getBannerURL())  }
    }.filterNotNull()

    val pagerState = rememberPagerState{ banners.size }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer),
    ) { imgIndex ->
        AsyncImage(
            model = banners[imgIndex],
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Banner", // TODO: LOCALIZE,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            onError = {
                // TODO: Handle this? or just ignore idk yet
                println("BANNER FAILED TO LOAD: URL: ${banners[imgIndex]}")
            })
    }

    // Loop through artworks
    LaunchedEffect(Unit) {
        while(true) {
            delay(nextBannerTimeout.milliseconds)

            val nextPage = (pagerState.currentPage + 1) % (banners.size) - 1
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(750))
        }
    }
}

@Composable
fun FocusedItem(
    item: Item,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(onBack, { println("HEART CLICKED") }) }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            // Banners start at the 'raw' top of screen
            Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(275.dp)
                        .blur(2.dp)
            ) { ShowBanner(item) }

            // TODO: Main content here
            Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 15.dp)
            ) {
                Box(modifier = Modifier.width(165.dp)) { ItemCard(item) }
            }
        }
    }
}