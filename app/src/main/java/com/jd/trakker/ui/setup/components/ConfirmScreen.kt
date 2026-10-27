package com.jd.trakker.ui.setup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jd.trakker.R

@Composable
fun ConfirmScreen() {
    Column(modifier = Modifier.padding(25.dp).fillMaxSize(),
           verticalArrangement = Arrangement.Top,
           horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.emoji_smile),
            modifier = Modifier.fillMaxSize(1/2f).padding(top = 50.dp),
            contentDescription = stringResource(R.string.smile_emoji)
        )


        Text(
            text = stringResource(R.string.setup_confirm_screen_text),
            textAlign = TextAlign.Center,
            lineHeight = 25.sp,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            modifier = Modifier.padding(bottom = 15.dp)
        )

        Text(
            text = stringResource(R.string.setup_more_on_settings),
            textAlign = TextAlign.Center,
            softWrap = true,
            style = MaterialTheme.typography.labelMedium
        )
    }
}