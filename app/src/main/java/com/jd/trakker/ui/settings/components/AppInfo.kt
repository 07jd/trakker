package com.jd.trakker.ui.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.BuildConfig
import com.jd.trakker.R
import com.jd.trakker.ui.theme.backgroundAppIconColor
import java.util.Locale

@Composable
fun AppInfo(modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App logo
        Box(
            modifier = Modifier
                    .weight(.5f)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(percent = 15))
                    .background(backgroundAppIconColor())
        ) {
            Image(
                painter = painterResource(R.drawable.logo_trakker),
                contentDescription = stringResource(R.string.app_name)
            )
        }

        // Details
        Column(
            modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(.5f),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start
        ) {
            // App name
            Text(
                text = stringResource(R.string.app_name),
                softWrap = true,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.headlineLargeEmphasized
            )

            // App version
            Text(
                text = "${BuildConfig.VERSION_NAME} " + BuildConfig.BUILD_TYPE.uppercase(Locale.US),
                softWrap = true,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}