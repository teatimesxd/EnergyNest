package com.example.energynest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.abs

private val BgColor = Color(0xFFF4F4F1)
private val CardColor = Color(0xFFFFFFFF)
private val CardBorder = Color(0xFFE9E9E4)
private val GreenDark = Color(0xFF008A5D)
private val GreenPrimary = Color(0xFF00B87C)
private val GreenLight = Color(0xFFE6F8F2)
private val GreenTrack = Color(0xFFE6F8F2)
private val TextPrimary = Color(0xFF15181C)
private val TextSecondary = Color(0xFF8A8D91)

enum class ConsumptionPeriod(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

private data class MonthRecord(
    val label: String,
    val shortLabel: String,
    val daysInMonth: Int,
    val totalEnergyKwh: Double,
    val usageBreakdown: List<Pair<String, Int>>
)

// Tariff / emission-factor constants used to derive cost & CO2 from energy usage.
private const val COST_PER_KWH_RM = 0.39
private const val CO2_PER_KWH_KG = 0.42

private val dailyShapeKwh = listOf(28.0, 46.0, 37.0, 60.0, 51.0, 65.0, 55.0, 76.0, 62.0, 86.0)
private val dailyShapeLabels = listOf("1", "4", "7", "10", "13", "16", "19", "22", "25", "28")
private val weeklyShapeFraction = listOf(0.22, 0.25, 0.24, 0.29)
private val weeklyLabels = listOf("Wk 1", "Wk 2", "Wk 3", "Wk 4")

private val monthRecords = listOf(
    MonthRecord(
        label = "Aug 2026", shortLabel = "Aug", daysInMonth = 31, totalEnergyKwh = 1248.0,
        usageBreakdown = listOf(
            "Air Conditioning" to 42, "Lighting" to 21, "Equipment" to 19,
            "Appliances" to 12, "Other" to 6
        )
    ),
    MonthRecord(
        label = "Jul 2026", shortLabel = "Jul", daysInMonth = 31, totalEnergyKwh = 1151.0,
        usageBreakdown = listOf(
            "Air Conditioning" to 39, "Lighting" to 23, "Equipment" to 20,
            "Appliances" to 12, "Other" to 6
        )
    ),
    MonthRecord(
        label = "Jun 2026", shortLabel = "Jun", daysInMonth = 30, totalEnergyKwh = 1102.0,
        usageBreakdown = listOf(
            "Air Conditioning" to 37, "Lighting" to 24, "Equipment" to 21,
            "Appliances" to 13, "Other" to 5
        )
    ),
    MonthRecord(
        label = "May 2026", shortLabel = "May", daysInMonth = 31, totalEnergyKwh = 986.0,
        usageBreakdown = listOf(
            "Air Conditioning" to 33, "Lighting" to 25, "Equipment" to 22,
            "Appliances" to 14, "Other" to 6
        )
    ),
    MonthRecord(
        label = "Apr 2026", shortLabel = "Apr", daysInMonth = 30, totalEnergyKwh = 915.0,
        usageBreakdown = listOf(
            "Air Conditioning" to 30, "Lighting" to 26, "Equipment" to 23,
            "Appliances" to 15, "Other" to 6
        )
    )
)

private data class ChartSeries(val values: List<Double>, val labels: List<String>)

private data class MonthlyReport(
    val label: String,
    val totalEnergyKwh: Double,
    val totalEnergyChangePct: Double?,
    val estimatedCostRM: Double,
    val avgDailyKwh: Double,
    val avgDailyChangePct: Double?,
    val co2Kg: Double,
    val usageBreakdown: List<Pair<String, Int>>,
    val daily: ChartSeries,
    val weekly: ChartSeries,
    val monthly: ChartSeries
)

private fun percentChange(current: Double, previous: Double?): Double? {
    if (previous == null || previous == 0.0) return null
    return ((current - previous) / previous) * 100.0
}

private fun buildReport(index: Int): MonthlyReport {
    val record = monthRecords[index]
    val previous = monthRecords.getOrNull(index + 1)

    val totalEnergy = record.totalEnergyKwh
    val avgDaily = totalEnergy / record.daysInMonth
    val previousAvgDaily = previous?.let { it.totalEnergyKwh / it.daysInMonth }

    val scale = totalEnergy / monthRecords.first().totalEnergyKwh
    val dailyValues = dailyShapeKwh.map { it * scale }

    val weeklyValues = weeklyShapeFraction.map { it * totalEnergy }

    val trendSlice = monthRecords.subList(index, monthRecords.size).reversed()

    return MonthlyReport(
        label = record.label,
        totalEnergyKwh = totalEnergy,
        totalEnergyChangePct = percentChange(totalEnergy, previous?.totalEnergyKwh),
        estimatedCostRM = totalEnergy * COST_PER_KWH_RM,
        avgDailyKwh = avgDaily,
        avgDailyChangePct = percentChange(avgDaily, previousAvgDaily),
        co2Kg = totalEnergy * CO2_PER_KWH_KG,
        usageBreakdown = record.usageBreakdown,
        daily = ChartSeries(dailyValues, dailyShapeLabels),
        weekly = ChartSeries(weeklyValues, weeklyLabels),
        monthly = ChartSeries(trendSlice.map { it.totalEnergyKwh }, trendSlice.map { it.shortLabel })
    )
}

private fun formatEnergy(kwh: Double): String = String.format(Locale.US, "%,.0f kWh", kwh)
private fun formatAvgDaily(kwh: Double): String = String.format(Locale.US, "%.1f kWh", kwh)
private fun formatCost(rm: Double): String = "RM " + String.format(Locale.US, "%,.2f", rm)
private fun formatCo2(kg: Double): String = String.format(Locale.US, "%,.0f kg", kg)
private fun formatPercent(pct: Double): String = String.format(Locale.US, "%.1f%%", abs(pct))

@Composable
fun ElectricAnalysisScreen(onBack: () -> Unit = {}) {
    var selectedMonthIndex by remember { mutableStateOf(0) }
    var selectedPeriod by remember { mutableStateOf(ConsumptionPeriod.DAILY) }
    val report = remember(selectedMonthIndex) { buildReport(selectedMonthIndex) }

    var dragAmount by remember { mutableStateOf(0f) }
    val swipeThreshold = 120f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .statusBarsPadding()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAmount = 0f },
                    onDragEnd = {
                        if (abs(dragAmount) > swipeThreshold) {
                            onBack()
                        }
                        dragAmount = 0f
                    },
                    onDragCancel = { dragAmount = 0f }
                ) { change, delta ->
                    change.consume()
                    dragAmount += delta
                }
            }
    ) {
        TopBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Monitor your electricity usage and performance",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(16.dp))
            MonthSelector(
                selectedLabel = monthRecords[selectedMonthIndex].label,
                allLabels = monthRecords.map { it.label },
                onSelect = { index -> selectedMonthIndex = index }
            )

            Spacer(Modifier.height(16.dp))
            StatsGrid(report)

            Spacer(Modifier.height(16.dp))
            EnergyConsumptionCard(
                report = report,
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it }
            )

            Spacer(Modifier.height(16.dp))
            UsageBreakdownCard(report.usageBreakdown)

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopBar() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardColor)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp))
            Text("Electric Analysis", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Box(modifier = Modifier.size(40.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )
    }
}

@Composable
private fun MonthSelector(
    selectedLabel: String,
    allLabels: List<String>,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "This Month",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Box {
            Row(
                modifier = Modifier.clickable { expanded = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedLabel,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Choose month",
                    tint = GreenPrimary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(CardColor)
            ) {
                allLabels.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                fontSize = 15.sp,
                                fontWeight = if (label == selectedLabel) FontWeight.Bold else FontWeight.Normal,
                                color = if (label == selectedLabel) GreenDark else TextPrimary
                            )
                        },
                        onClick = {
                            onSelect(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsGrid(report: MonthlyReport) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Total Energy",
                value = formatEnergy(report.totalEnergyKwh),
                changePct = report.totalEnergyChangePct
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Estimated Cost",
                value = formatCost(report.estimatedCostRM),
                changePct = report.totalEnergyChangePct
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Average Daily",
                value = formatAvgDaily(report.avgDailyKwh),
                changePct = report.avgDailyChangePct
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "CO\u2082 Emissions",
                value = formatCo2(report.co2Kg),
                changePct = report.totalEnergyChangePct
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    changePct: Double?
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(6.dp))
        if (changePct != null) {
            Text(
                text = (if (changePct >= 0) "\u2191 " else "\u2193 ") + formatPercent(changePct),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = GreenPrimary
            )
        } else {
            Text(
                text = "No prior data",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun EnergyConsumptionCard(
    report: MonthlyReport,
    selectedPeriod: ConsumptionPeriod,
    onPeriodSelected: (ConsumptionPeriod) -> Unit
) {
    val series = when (selectedPeriod) {
        ConsumptionPeriod.DAILY -> report.daily
        ConsumptionPeriod.WEEKLY -> report.weekly
        ConsumptionPeriod.MONTHLY -> report.monthly
    }
    val compareText = when (selectedPeriod) {
        ConsumptionPeriod.DAILY -> "vs last month"
        ConsumptionPeriod.WEEKLY -> "vs last month"
        ConsumptionPeriod.MONTHLY -> "over ${series.values.size} months"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardColor)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Energy Consumption",
                modifier = Modifier.weight(1f, fill = false),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                softWrap = false
            )
            Spacer(Modifier.width(8.dp))
            SegmentedTabs(selectedPeriod = selectedPeriod, onSelected = onPeriodSelected)
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = formatEnergy(report.totalEnergyKwh),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        if (report.totalEnergyChangePct != null) {
            Text(
                text = (if (report.totalEnergyChangePct >= 0) "\u2191 " else "\u2193 ") +
                        "${formatPercent(report.totalEnergyChangePct)} $compareText",
                fontSize = 13.sp,
                color = GreenPrimary
            )
        } else {
            Text(text = "No prior data to compare", fontSize = 13.sp, color = TextSecondary)
        }

        Spacer(Modifier.height(16.dp))
        BarChart(values = series.values, labels = series.labels)
    }
}

@Composable
private fun SegmentedTabs(
    selectedPeriod: ConsumptionPeriod,
    onSelected: (ConsumptionPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BgColor)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConsumptionPeriod.values().forEach { period ->
            TabChip(
                text = period.label,
                selected = period == selectedPeriod,
                onClick = { onSelected(period) }
            )
        }
    }
}

@Composable
private fun TabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) GreenLight else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) GreenDark else TextSecondary,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun BarChart(values: List<Double>, labels: List<String>) {
    val maxValue = (values.maxOrNull() ?: 1.0).coerceAtLeast(0.0001)
    val fractions = values.map { (it / maxValue).toFloat().coerceIn(0.05f, 1f) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            fractions.forEach { fraction ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(GreenPrimary)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    fontSize = 10.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun UsageBreakdownCard(items: List<Pair<String, Int>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardColor)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Usage Breakdown",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Where your electricity is going",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(Modifier.height(16.dp))

        items.forEachIndexed { index, (label, percent) ->
            UsageRow(label = label, percent = percent, fraction = percent / 100f)
            if (index != items.lastIndex) {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun UsageRow(label: String, percent: Int, fraction: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 14.sp, color = TextPrimary)
            Text(
                text = "$percent%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
        Spacer(Modifier.height(8.dp))
        UsageProgressBar(fraction = fraction)
    }
}

@Composable
private fun UsageProgressBar(fraction: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
    ) {
        val cornerRadius = CornerRadius(size.height / 2, size.height / 2)

        drawRoundRect(
            color = GreenTrack,
            size = size,
            cornerRadius = cornerRadius
        )

        val fillWidth = (size.width * fraction).coerceAtLeast(size.height)
        val fillPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, Size(fillWidth, size.height)),
                    topLeft = cornerRadius,
                    bottomLeft = cornerRadius,
                    topRight = CornerRadius.Zero,
                    bottomRight = CornerRadius.Zero
                )
            )
        }
        drawPath(path = fillPath, color = GreenPrimary)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun ElectricAnalysisScreenPreview() {
    MaterialTheme {
        ElectricAnalysisScreen()
    }
}