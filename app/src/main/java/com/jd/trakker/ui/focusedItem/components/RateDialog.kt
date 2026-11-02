package com.jd.trakker.ui.focusedItem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
    var raw by rememberSaveable { mutableStateOf(value?.toString() ?: "") }
    val rating = raw.toDoubleOrNull()

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
                    modifier = Modifier.padding(top = 16.dp)
                )


                OutlinedTextField(
                    value = raw,
                    modifier = Modifier.fillMaxWidth(3/4f),
                    placeholder = {
                        Text(
                            text = "7.0",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    supportingText = {
                        Text(
                            text = "0 – 10",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    onValueChange = {
                        val parsed = it.toDoubleOrNull()
                        when {
                            (parsed == null && it.isEmpty()) ||
                            (parsed != null && parsed in 0.0..10.0 && it.length <= 4) -> {
                                raw = it
                            }
                            else -> {}
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End)
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(
                            text = stringResource(R.string.go_back)
                        )
                    }

                    TextButton(
                        enabled = !raw.isEmpty(),
                        onClick = { onRate(rating); onDismiss() }
                    ) {
                        Text(
                            text = stringResource(R.string.rate)
                        )
                    }
                }


            }
        }
    }
}