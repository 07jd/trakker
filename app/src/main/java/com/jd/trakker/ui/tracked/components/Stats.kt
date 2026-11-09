package com.jd.trakker.ui.tracked.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jd.trakker.R
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.item.toStringRes
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.local.tracked.toStringRes
import com.jd.trakker.ui.tracked.Stats
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.data.PieChartModelProducer
import com.patrykandpatrick.vico.compose.pie.data.pieModel
import com.patrykandpatrick.vico.compose.pie.rememberPieChart

enum class StatsType {
    MEAN,
    COUNT,
    GENRES,
    CATEGORY
}

@StringRes
fun StatsType.toStringRes(): Int = when(this) {
    StatsType.MEAN     -> R.string.meam
    StatsType.COUNT    -> R.string.count
    StatsType.CATEGORY -> R.string.category
    StatsType.GENRES   -> R.string.genres
}

@Composable
fun Stats(
    data: Stats,
    height: Dp = 230.dp
) {
    var selected by remember { mutableStateOf(StatsType.COUNT) }
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
    )

    Column(
        modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = height)
    ) {
        // PREV <LABEL> NEXT
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back
            TextButton(onClick = {
                val newOrdinal = if ((selected.ordinal-1) < 0) StatsType.entries.size - 1 else selected.ordinal - 1
                selected = StatsType.entries[newOrdinal]
            }) {
                Text(text = stringResource(R.string.go_back))
            }

            // Label
            Text(
                text = stringResource(selected.toStringRes()),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            // Next
            TextButton(onClick = {
                selected = StatsType.entries[(selected.ordinal+1) % StatsType.entries.size]
            }) {
                Text(text = stringResource(R.string.setup_next))
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when(selected) {
                StatsType.MEAN     -> MeanChart(data.meanRating, data.meanUserRating)
                StatsType.COUNT    -> {
                    var colorIndex = 0
                    val entries = data.count.map { (type, count) ->
                        val entry = Triple(
                            stringResource(type.toStringRes()),
                            count.toDouble(),
                            colors[colorIndex]
                        )

                        colorIndex += 1
                        if (colorIndex >= colors.size) colorIndex = 0

                        entry
                    }

                    StatsPieChart(entries)
                }
                StatsType.GENRES   -> {
                    val othersLabel = stringResource(R.string.others)
                    val othersColor = Color.Gray

                    val sorted = data.genresCount.entries.sortedByDescending { it.value }
                    val topThree = sorted.take(3).mapIndexed { index, value ->
                        Triple(value.key, value.value.toDouble(), colors[index])
                    }.toMutableList()
                    val othersCount = sorted.drop(3).sumOf { it.value }.toDouble()

                    if(othersCount > 0) topThree.add(Triple(othersLabel, othersCount, othersColor))
                    StatsPieChart(entries = topThree)
                }
                StatsType.CATEGORY -> {
                    val dataCategories = TrackedStatus.entries
                            .filter { (data.categoryCount[it] ?: 0) > 0 }
                            .mapIndexed { index, type ->
                                Triple(
                                    stringResource(type.toStringRes()),
                                    data.categoryCount[type]!!.toDouble(),
                                    colors[index]
                                )
                            }

                    StatsPieChart(entries = dataCategories)
                }
            }
        }
    }
}

@Composable
private fun StatsPieChart(
    // Label, Data, Color
    entries: List<Triple<String, Double, Color>>,
    height: Dp = 220.dp
) {
    if (entries.isEmpty()) {
        Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_items)
            )
        }
    } else {
        val modelProducer = remember { PieChartModelProducer() }
        LaunchedEffect(entries) {
            modelProducer.runTransaction {
                pieModel {
                    series(entries.map { it.second })
                }
            }
        }


        val chart = rememberPieChart(
            sliceProvider = PieChart.SliceProvider.series(
                entries.map { (_, _, color) ->
                    PieChart.Slice(
                        fill = Fill(color)
                    )
                }
            ),
            spacing = if(entries.size > 1) 2.dp else 0.dp
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PieChartHost(
                chart = chart,
                modelProducer = modelProducer,
                modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
            )

            Column(
                modifier = Modifier
                        .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                entries.forEach { (label, _, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                    .size(12.dp)
                                    .background(color, CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = label,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// This function is llm generated
@Composable
private fun MeanChart(
    rating: Map<ItemType, Double>,
    userRating: Map<ItemType, Double>
) {
    // Filter items with 0 rating
    val types = ItemType.entries.filter {
        (rating[it] ?: 0.0) > 0.0 || (userRating[it] ?: 0.0) > 0.0
    }
    val labels = types.map { stringResource(it.toStringRes()) }

    if (types.isEmpty()) {
        Text(text = stringResource(R.string.no_items))
        return
    }

    val apiColor  = MaterialTheme.colorScheme.primary
    val userColor = MaterialTheme.colorScheme.tertiary

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(types, rating, userRating) {
        modelProducer.runTransaction {
            columnModel {
                series(types.map { rating[it] ?: 0.0 })
                series(types.map { userRating[it] ?: 0.0 })
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series (
                        rememberLineComponent(fill = Fill(apiColor), thickness = 16.dp),
                        rememberLineComponent(fill = Fill(userColor), thickness = 16.dp)
                    ),
                    rangeProvider = CartesianLayerRangeProvider.fixed (minY = 0.0)
                ),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = { _, value, _ -> value.toInt().toString() }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, value, _ ->
                        labels.getOrNull(value.toInt()) ?: ""
                    }
                )
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
        )

        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MeanLegendItem(color = apiColor, label = stringResource(R.string.average_rating))
            Spacer(Modifier.width(16.dp))
            MeanLegendItem(color = userColor, label = stringResource(R.string.average_user_rating))
        }
    }
}

// This function is llm generated
@Composable
private fun MeanLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}
