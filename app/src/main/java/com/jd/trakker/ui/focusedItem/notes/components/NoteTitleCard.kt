package com.jd.trakker.ui.focusedItem.notes.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.R

@Composable
fun NoteTitleCard(
    title: String,
    index: Int,
    onSelect: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 12.dp, end = 12.dp),
         onClick = { onSelect(index) },
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Note title
            Box(modifier = Modifier.weight(.85f)) {
                Text(text = if(title.isBlank() || title.isEmpty())
                    stringResource(R.string.untitled_note, index+1)
                else title,
                     modifier = Modifier.padding(16.dp).basicMarquee(Int.MAX_VALUE),
                     textAlign = TextAlign.Start
                )
            }

            // Remove note button
            Box(
                modifier = Modifier
                        .weight(.15f)
                        .padding(end = 12.dp)
                        .clickable{ onDelete(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = stringResource(R.string.delete_note),
                )
            }
        }
    }
}