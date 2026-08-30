package com.example.energynest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewSidebar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---- Global Supabase Client Initialization ----
val supabase = createSupabaseClient(
    supabaseUrl = "https://byrlgjgjzcwwdpnznuiq.supabase.co",
    supabaseKey = "sb_publishable_FV9wvw0kv59rBrHec9BNxA_moyCubSv"
) {
    install(Postgrest)
}

// In-memory cache to keep data when switching tabs
private var cachedStats: HomeEnergyStats? = null

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreenColour = Color(0xFF00B87C)
private val LightGreenBg = Color(0xFFD8F3E5)
private val ProgressBg = Color(0xFFE5E7EB)
private val AvatarBg = Color(0xFFE6E8EA)
private val BorderLight = Color(0xFFE2E8F0)

// ---- Data Model ----
@Serializable
data class HomeEnergyStats(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String = "demo_user",
    @SerialName("date") val date: String? = null,
    @SerialName("generated_kwh") val generatedKwh: Double = 0.0,
    @SerialName("stored_energy_pct") val storedEnergyPct: Double = 0.0,
    @SerialName("stored_energy_kwh") val storedEnergyKwh: Double = 0.0,
    @SerialName("estimated_usage_mins") val estimatedUsageMins: Int = 0,
    @SerialName("carbon_reduced_kg") val carbonReducedKg: Double = 0.0,
    @SerialName("today_savings_myr") val todaySavingsMyr: Double = 0.0
)

@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit = {}
) {
    // Initialize state from cache to instantly load when switching back to Home tab
    var stats by remember { mutableStateOf(cachedStats ?: HomeEnergyStats()) }
    var isLoading by remember { mutableStateOf(cachedStats == null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchHomeStats() {
        // Skip network fetch if data has already been fetched during this session
        if (cachedStats != null) {
            isLoading = false
            return
        }

        try {
            isLoading = true
            errorMessage = null

            val result = withContext(Dispatchers.IO) {
                supabase.from("home_energy_stats")
                    .select {
                        filter {
                            eq("user_id", "demo_user")
                        }
                        order("date", order = Order.DESCENDING)
                    }
                    .decodeList<HomeEnergyStats>()
                    .firstOrNull()
            }

            if (result != null) {
                stats = result
                cachedStats = result // Update in-memory cache
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
        val durationFormatted = remember(stats.estimatedUsageMins) {
            val hours = stats.estimatedUsageMins / 60
            val mins = stats.estimatedUsageMins % 60
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
            // Top App Bar
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
                                imageVector = Icons.AutoMirrored.Outlined.ViewSidebar,
                                contentDescription = "Sidebar",
                                tint = TextDark
                            )
                        }
                        IconButton(onClick = { /* Open Notifications */ }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = TextDark
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = BorderLight)
                }
            }

            // Main Content Container
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Greeting Header & Profile Avatar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hello, Homeowner",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = "Location",
                                    tint = TextGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Peninsular Malaysia",
                                    fontSize = 14.sp,
                                    color = TextGray
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AvatarBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = TextDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Main Circular Gauge
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
                                    imageVector = Icons.Outlined.SolarPower,
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

                    // Stored Energy Row
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
                                imageVector = Icons.Outlined.BatteryChargingFull,
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

                    // Estimated Usage Duration (Insight Box)
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
                            imageVector = Icons.Outlined.Lightbulb,
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

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Eco,
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
                                    text = "${stats.carbonReducedKg.toInt()}kg",
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
                                imageVector = Icons.Outlined.Savings,
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
                                text = "RM ${String.format("%.2f", stats.todaySavingsMyr)}",
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}