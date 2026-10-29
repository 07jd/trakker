package com.jd.trakker.ui.focusedItem.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import androidx.core.net.toUri

@Composable
fun TopBar(url: String, alpha: Float, onBack: () -> Unit) {
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
            modifier = Modifier.width(150.dp).size(48.dp, 48.dp).clickable{ onBack() },
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(R.drawable.chevron_double_left),
                contentDescription = stringResource(R.string.go_back),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(23.dp)
            )
        }



        Box(
            modifier = Modifier
                    .width(150.dp)
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