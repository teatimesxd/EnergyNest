package com.example.energynest
// only demo user here , need to change to actual user when login database connected
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private var cachedStats: HomeStats? = null

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreenColour = Color(0xFF00B87C)
private val LightGreenBg = Color(0xFFD8F3E5)
private val ProgressBg = Color(0xFFE5E7EB)
private val BorderLight = Color(0xFFE2E8F0)

@Composable
fun HomeScreen(
    userIc: String,
    onOpenDrawer: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var stats by remember { mutableStateOf(cachedStats ?: HomeStats(icNumber = "demo", date = "", generatedKwh = 0.0, storedEnergyPct = 0.0, storedEnergyKwh = 0.0, estimatedUsageDuration = 0.0, co2Emission = 0.0, totalSavings = 0.0)) }
    var isLoading by remember { mutableStateOf(cachedStats == null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchHomeStats() {
        if (cachedStats != null) {
            isLoading = false
            return
        }

        try {
            isLoading = true
            errorMessage = null

            val result = withContext(Dispatchers.IO) {
                SupabaseClient.client.from("Home")
                    .select {
                        filter { eq("ic_number", userIc) } 
                        order("date", order = Order.DESCENDING)
                    }
                    .decodeList<HomeStats>()
                    .firstOrNull()
            }

            if (result != null) {
                stats = result
                cachedStats = result
            }
        } catch (e: Exception) {
            errorMessage = "Fetch failed: " + e.message
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchHomeStats()
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandGreenColour)
        }
    } else {
        val durationFormatted = remember(stats.estimatedUsageDuration) {
            val totalMins = stats.estimatedUsageDuration.toInt()
            val hours = totalMins / 60
            val mins = totalMins % 60
            when {
                hours > 0 && mins > 0 -> "$hours hours and $mins minutes."
                hours > 0 -> "$hours hours."
                else -> "$mins minutes."
            }
        }

        val estimatedUsageText = remember(durationFormatted) {
            buildAnnotatedString {
                append("Based on your daily average consumption, your stored energy can power your home for another ")
                withStyle(style = SpanStyle(color = BrandGreenColour, fontWeight = FontWeight.Bold)) {
                    append(durationFormatted)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ---- Top App Bar ----
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                painter = painterResource(id = R.drawable.sidebar_icon),
                                contentDescription = "Sidebar",
                                tint = TextDark
                            )
                        }

                        // ---- Profile Icon ----
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

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Hello, Homeowner",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .border(width = 16.dp, color = BrandGreenColour, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.solar_power_icon),
                                    contentDescription = "Solar Generated",
                                    tint = BrandGreenColour,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "GENERATED",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stats.generatedKwh.toString(),
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = " kWh",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextDark,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Today",
                                    fontSize = 14.sp,
                                    color = TextGray
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(LightGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.battery_icon),
                                contentDescription = "Stored Energy Icon",
                                tint = BrandGreenColour,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Stored Energy",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${(stats.storedEnergyPct * 100).toInt()}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "(${stats.storedEnergyKwh} kWh)",
                                    fontSize = 14.sp,
                                    color = TextGray
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { stats.storedEnergyPct.toFloat() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(8.dp)
                                .clip(CircleShape),
                            color = BrandGreenColour,
                            trackColor = ProgressBg,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(72.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(BrandGreenColour)
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.lightbulb_icon),
                            contentDescription = "Insight Icon",
                            tint = BrandGreenColour,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Estimated Usage Duration",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGray
                            )
                            Text(
                                text = estimatedUsageText,
                                fontSize = 13.sp,
                                color = TextDark,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.eco_icon),
                                contentDescription = "Carbon Reduced",
                                tint = TextDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Carbon Reduced",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${stats.co2Emission.toInt()}kg",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CO2",
                                    fontSize = 14.sp,
                                    color = TextGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.saving_icon),
                                contentDescription = "Today's Savings",
                                tint = TextDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Today's Savings",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                            Text(
                                text = "RM ${String.format("%.2f", stats.totalSavings)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }
    }
}