package com.jd.trakker.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jd.trakker.R

/*
 * FILTER OPTIONS
 */

enum class FilterType {
    RELEVANCE,
    RATING,
    ALPHABETICAL,
    RELEASE_DATE
}

enum class FilterOrder {
    ASCENDANT,
    DESCENDANT
}

object FilterDefaults {
    val order   = FilterOrder.DESCENDANT
    val type    = FilterType.RELEVANCE
}

@StringRes
fun FilterType.toStringRes(): Int = when(this) {
    FilterType.RELEVANCE    -> R.string.filter_relevance
    FilterType.RATING       -> R.string.filter_rating
    FilterType.ALPHABETICAL -> R.string.filter_alphabetical
    FilterType.RELEASE_DATE -> R.string.filter_release_date
}

@StringRes
fun FilterOrder.toStringRes(): Int = when(this) {
    FilterOrder.ASCENDANT  -> R.string.ascendent
    FilterOrder.DESCENDANT -> R.string.descendent
}


/*
 * FILTER IMPL
 */
@Composable
fun Filter(
    filterType: FilterType,
    filterOrder: FilterOrder,
    onApplyFilter: (FilterOrder, FilterType) -> Unit,
    onDismiss: () -> Unit
) {
    val cardHeight = 50.dp
    val endListingPadding = cardHeight/2

    var newFilterType   by remember { mutableStateOf(filterType) }
    var newFilterOrder  by remember { mutableStateOf(filterOrder) }

    @OptIn(ExperimentalMaterial3Api::class)
    ModalBottomSheet(
        sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden),
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // RESET <FILTER> APPLY
            Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onApplyFilter(FilterDefaults.order, FilterDefaults.type) }) {
                    Text(text = stringResource(R.string.reset))
                }

                Text(
                    text = stringResource(R.string.filter),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )

                TextButton(onClick = { onApplyFilter(newFilterOrder, newFilterType) }
                ) {
                    Text(text = stringResource(R.string.apply))
                }
            }

            HorizontalDivider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Filter Order
            Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
            ) {
                // Label
                Text(
                    text = stringResource(R.string.order),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )

                FilterOrder.entries.forEach {
                    InteractiveCard(
                        title = stringResource(it.toStringRes()),
                        cardHeight = cardHeight,
                        highlightCard = it == newFilterOrder,
                        onCardClick = { newFilterOrder = it }
                    )
                }

                Spacer(modifier = Modifier.height(endListingPadding))
            }

            HorizontalDivider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Filter Type
            Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
            ) {
                // Label
                Text(
                    text = stringResource(R.string.type),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FilterType.entries.forEach {
                    InteractiveCard(
                        title = stringResource(it.toStringRes()),
                        cardHeight = cardHeight,
                        highlightCard = it == newFilterType,
                        onCardClick = { newFilterType = it }
                    )
                }

                Spacer(modifier = Modifier.height(endListingPadding))
            }
        }
    }
}