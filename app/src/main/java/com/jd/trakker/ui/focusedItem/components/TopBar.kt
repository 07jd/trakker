package com.jd.trakker.ui.focusedItem.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import androidx.core.net.toUri

@Composable
fun TopBar(
    title: String,
    url: String,
    alpha: Float,
    onBack: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val context = LocalContext.current

    Row(
        modifier = Modifier
                .fillMaxWidth()
                .background(bg.copy(alpha = alpha))
                .statusBarsPadding()
                .padding(horizontal = 30.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp, 48.dp).clickable{ onBack() },
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(R.drawable.chevron_double_left),
                contentDescription = stringResource(R.string.go_back),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(23.dp)
            )
        }

        // Show item title when scrolled enough
        AnimatedVisibility(
            visible = alpha > 0.7f,
            enter   = scaleIn() + fadeIn(),
            exit    = scaleOut() + fadeOut()
        ) {
            Box(modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp, end = 6.dp)
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                    softWrap = false,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Box(
            modifier = Modifier
                    .size(48.dp, 48.dp)
                    .clickable{
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(browserIntent)
                        } catch(e: Exception) {
                            println("Failed to open link \"$url\" in browser, error: ${e.message}")
                        }
                    },
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = painterResource(R.drawable.globe),
                contentDescription = stringResource(R.string.visit_media_page),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(23.dp)
            )
        }

    }
}