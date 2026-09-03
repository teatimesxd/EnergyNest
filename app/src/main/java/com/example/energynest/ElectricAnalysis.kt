package com.example.energynest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.abs

private val BgColor = Color(0xFFF6F8F7)
private val CardColor = Color(0xFFFFFFFF)
private val CardBorder = Color(0xFFE2E8F0)
private val BorderLight = Color(0xFFE2E8F0)
private val GreenDark = Color(0xFF008A5D)
private val GreenPrimary = Color(0xFF00B87C)
private val GreenLight = Color(0xFFE6F8F2)
private val GreenTrack = Color(0xFFE6F8F2)
private val TextPrimary = Color(0xFF191C1E)
private val TextDark = Color(0xFF191C1E)
private val TextSecondary = Color(0xFF5A6065)

enum class ConsumptionPeriod(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

private data class ChartSeries(val values: List<Double>, val labels: List<String>)

private data class MonthlyReport(
    val label: String,
    val totalGenerated: Double,
    val totalGeneratedChangePct: Double?,
    val carbonReduced: Double,
    val carbonReducedChangePct: Double?,
    val todaySavingsRM: Double,
    val todaySavingsChangePct: Double?,
    val averageDaily: Double,
    val averageDailyChangePct: Double?,
    val totalUsageKwh: Double, 
    val usageBreakdown: List<Pair<String, Int>>,
    val daily: ChartSeries,
    val weekly: ChartSeries,
    val monthly: ChartSeries
)

private fun percentChange(current: Double, previous: Double?): Double? {
    if (previous == null || previous == 0.0) return null
    return ((current - previous) / previous) * 100.0
}

private fun buildReport(
    record: ElectricUsage,
    previous: ElectricUsage?,
    allRecords: List<ElectricUsage>,
    latestHomeHistory: List<HomeStats>
): MonthlyReport {
    val isLatest = allRecords.firstOrNull() == record
    
    val totalGen: Double
    val carbon: Double
    val savings: Double
    val avgDaily: Double

    if (isLatest && latestHomeHistory.isNotEmpty()) {
        totalGen = latestHomeHistory.sumOf { it.generatedKwh }
        carbon = latestHomeHistory.sumOf { it.co2Emission }
        savings = latestHomeHistory.sumOf { it.totalSavings }
        avgDaily = totalGen / latestHomeHistory.size 
    } else {
        totalGen = record.totalEnergyKwh
        carbon = record.co2Emission
        savings = record.estimatedCost
        avgDaily = record.averageDaily
    }

    val usageBreakdown = listOf(
        "Air Conditioning" to (record.acPercent ?: 0),
        "Lighting" to (record.lightingPercent ?: 0),
        "Equipment" to (record.equipmentPercent ?: 0),
        "Appliances" to (record.appliancePercent ?: 0),
        "Other" to (record.otherPercent ?: 0)
    )

    // --- DAILY DATA ---
    val dailyValues: List<Double>
    val dailyLabels: List<String>
    if (isLatest && latestHomeHistory.isNotEmpty()) {
        val history = latestHomeHistory.take(10).reversed()
        dailyValues = history.map { it.generatedKwh }
        dailyLabels = history.map { it.date.split("-").last() } 
    } else {
        dailyValues = emptyList()
        dailyLabels = emptyList()
    }

    // --- WEEKLY DATA ---
    val weeklyValues: List<Double>
    val weeklyLabels: List<String>
    if (isLatest && latestHomeHistory.isNotEmpty()) {
        val chunks = latestHomeHistory.chunked(7).take(4).reversed()
        weeklyValues = chunks.map { week -> week.sumOf { it.generatedKwh } }
        weeklyLabels = List(weeklyValues.size) { "Wk ${weeklyValues.size - it}" }.reversed()
    } else {
        weeklyValues = emptyList()
        weeklyLabels = emptyList()
    }

    // --- MONTHLY DATA ---
    val trendSlice = allRecords.take(5).reversed()
    val monthlySeries = ChartSeries(
        values = trendSlice.map { it.totalEnergyKwh },
        labels = trendSlice.map { it.monthLabel }
    )

    return MonthlyReport(
        label = record.monthLabel,
        totalGenerated = totalGen,
        totalGeneratedChangePct = percentChange(totalGen, previous?.totalEnergyKwh),
        carbonReduced = carbon,
        carbonReducedChangePct = percentChange(carbon, previous?.co2Emission),
        todaySavingsRM = savings,
        todaySavingsChangePct = percentChange(savings, previous?.estimatedCost),
        averageDaily = avgDaily,
        averageDailyChangePct = percentChange(avgDaily, previous?.averageDaily),
        totalUsageKwh = totalGen,
        usageBreakdown = usageBreakdown,
        daily = ChartSeries(dailyValues, dailyLabels),
        weekly = ChartSeries(weeklyValues, weeklyLabels),
        monthly = monthlySeries
    )
}

private fun formatEnergy(kwh: Double): String = String.format(Locale.US, "%.1f kWh", kwh)
private fun formatAvgDaily(kwh: Double): String = String.format(Locale.US, "%.1f kWh", kwh)
private fun formatCost(rm: Double): String = "RM " + String.format(Locale.US, "%,.2f", rm)
private fun formatCo2(kg: Double): String = "${kg.toInt()} kg"
private fun formatPercent(pct: Double): String = String.format(Locale.US, "%.1f%%", abs(pct))

@Composable
fun ElectricAnalysisScreen(
    userIc: String = "",
    onOpenDrawer: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var usageRecords by remember { mutableStateOf<List<ElectricUsage>>(emptyList()) }
    var homeHistory by remember { mutableStateOf<List<HomeStats>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedMonthIndex by remember { mutableIntStateOf(0) }
    var selectedPeriod by remember { mutableStateOf(ConsumptionPeriod.DAILY) }

    val isPreview = LocalInspectionMode.current

    LaunchedEffect(userIc) {
        if (isPreview) {
            usageRecords = listOf(
                ElectricUsage(null, userIc, "Aug 2026", 1248.0, 486.0, 40.0, 524.0, 42, 21, 19, 12, 6),
                ElectricUsage(null, userIc, "Jul 2026", 1151.0, 448.0, 37.0, 483.0, 40, 23, 20, 11, 6)
            )
            homeHistory = List(14) { i ->
                HomeStats(i, userIc, "2026-08-${31-i}", 40.0 + i % 10, 85.0, 12.2, 450.0, 524.0, 486.0)
            }
            isLoading = false
            return@LaunchedEffect
        }

        if (userIc.isEmpty()) {
            isLoading = false
            return@LaunchedEffect
        }
        try {
            isLoading = true
            val (uResults, hResults) = withContext(Dispatchers.IO) {
                val u = SupabaseClient.client.from("Electric_usage")
                    .select { filter { eq("ic_number", userIc) } }
                    .decodeList<ElectricUsage>()
                
                val h = SupabaseClient.client.from("Home")
                    .select {
                        filter { eq("ic_number", userIc) }
                        order("date", order = Order.DESCENDING)
                        limit(31) 
                    }.decodeList<HomeStats>()
                
                Pair(u, h)
            }
            usageRecords = uResults.sortedByDescending { it.usageId ?: 0 }
            homeHistory = hResults
        } catch (e: Exception) {
            // Error handling
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(BgColor), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenPrimary)
        }
        return
    }

    if (usageRecords.isEmpty() && homeHistory.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) 
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.battery_icon),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.LightGray.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text("No energy usage data found.", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("Start generating energy to see your analysis here.", fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
        return
    }

    // Always Prepend Live "Current" Snapshot for Comparison
    val effectiveRecords = remember(usageRecords, homeHistory) {
        if (homeHistory.isNotEmpty()) {
            val totalGen = homeHistory.sumOf { it.generatedKwh }
            val totalSavings = homeHistory.sumOf { it.totalSavings }
            val avgDaily = totalGen / homeHistory.size
            val totalCo2 = homeHistory.sumOf { it.co2Emission }
            
            val liveRecord = ElectricUsage(
                icNumber = userIc,
                monthLabel = "Current",
                totalEnergyKwh = totalGen,
                estimatedCost = totalSavings,
                averageDaily = avgDaily,
                co2Emission = totalCo2
            )
            // Combine: Live Snapshot + DB History
            listOf(liveRecord) + usageRecords
        } else {
            usageRecords
        }
    }

    val report = remember(selectedMonthIndex, effectiveRecords, homeHistory) {
        val current = effectiveRecords.getOrNull(selectedMonthIndex) ?: effectiveRecords.first()
        val previous = effectiveRecords.getOrNull(selectedMonthIndex + 1)
        buildReport(current, previous, effectiveRecords, homeHistory)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        TopBar(
            onOpenDrawer = onOpenDrawer,
            onProfileClick = onProfileClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Monitor your electricity usage and performance",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(16.dp))
            MonthSelector(
                selectedLabel = effectiveRecords[selectedMonthIndex].monthLabel,
                allLabels = effectiveRecords.map { it.monthLabel },
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
private fun TopBar(
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(CardColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        painter = painterResource(id = R.drawable.sidebar_icon),
                        contentDescription = "Sidebar",
                        tint = TextDark
                    )
                }
                Text(
                    text = "Electric Analysis",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile",
                    tint = TextDark
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = BorderLight)
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
                label = "Total Generated",
                value = formatEnergy(report.totalGenerated),
                changePct = report.totalGeneratedChangePct
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Today's Savings",
                value = formatCost(report.todaySavingsRM),
                changePct = report.todaySavingsChangePct
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Average Daily",
                value = formatAvgDaily(report.averageDaily),
                changePct = report.averageDailyChangePct
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Carbon Reduced",
                value = formatCo2(report.carbonReduced),
                changePct = report.carbonReducedChangePct
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
                text = (if (changePct >= 0) "↑ " else "↓ ") + formatPercent(changePct),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (changePct >= 0) GreenPrimary else Color.Red
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
        ConsumptionPeriod.WEEKLY -> "vs last week"
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
            text = formatEnergy(report.totalUsageKwh),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        if (report.totalGeneratedChangePct != null) {
            Text(
                text = (if (report.totalGeneratedChangePct >= 0) "↑ " else "↓ ") +
                        "${formatPercent(report.totalGeneratedChangePct)} $compareText",
                fontSize = 13.sp,
                color = if (report.totalGeneratedChangePct >= 0) GreenPrimary else Color.Red
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
        ConsumptionPeriod.entries.forEach { period ->
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
    if (values.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Data not available for this period.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
        return
    }

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

        if (fraction > 0f) {
            val fillWidth = (size.width * fraction).coerceAtLeast(size.height)
            val fillPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(Offset.Zero, Size(fillWidth, size.height)),
                        topLeft = cornerRadius,
                        bottomLeft = cornerRadius,
                        topRight = if (fraction >= 1f) cornerRadius else CornerRadius.Zero,
                        bottomRight = if (fraction >= 1f) cornerRadius else CornerRadius.Zero
                    )
                )
            }
            drawPath(path = fillPath, color = GreenPrimary)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun ElectricAnalysisScreenPreview() {
    MaterialTheme {
        ElectricAnalysisScreen()
    }
}
