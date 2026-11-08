package com.jd.trakker.ui.focusedItem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.jd.trakker.R

@Composable
fun RateDialog(
    value: Double? = null,
    onDismiss: () -> Unit,
    onRate: (Double?) -> Unit
) {
    var rating by remember(value) { mutableFloatStateOf(value?.toFloat() ?: 7.0f) }
    val sliderState = rememberSliderState(
        value = rating,
        steps = (10*4)-1,
        trackRange = 0f..10f
    )

    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        modifier = Modifier.fillMaxWidth(.9f),
        properties = DialogProperties(),
    ) {
        Surface(
            modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.rate_it),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Column(
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "%.2f/10.0".format(rating),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Slider(
                        state = sliderState,
                        onValueChange = { rating = it;  sliderState.value = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Remove rating
                    TextButton(
                        onClick = { onRate(null); onDismiss() }
                    ) {
                        Text(
                            text = stringResource(R.string.reset),
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        // Go back button
                        TextButton(
                            onClick = { onDismiss() }
                        ) {
                            Text(text = stringResource(R.string.go_back))
                        }

                        // Submit rate button
                        TextButton(
                            onClick = { onRate(rating.toDouble()); onDismiss() }
                        ) {
                            Text(text = stringResource(R.string.rate))
                        }
                    }
                }
            }
        }
    }
}