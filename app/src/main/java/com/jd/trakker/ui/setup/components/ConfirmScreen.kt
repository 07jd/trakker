package com.jd.trakker.ui.setup.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jd.trakker.R

@Composable
fun ConfirmScreen() {
    Row(
        modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.setup_confirm_screen_text),
            lineHeight = 25.sp,
        )
    }
}