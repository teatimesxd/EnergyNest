package com.example.energynest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AltRoute
import androidx.compose.material.icons.automirrored.outlined.ViewSidebar
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreenColour = Color(0xFF00B87C)
private val LightGreenBg = Color(0xFFD8F3E5)
private val ProgressBg = Color(0xFFE5E7EB)
private val CardBorderColor = Color(0xFFE2E8F0)
private val ActiveBlue = Color(0xFF2563EB)
private val CreditBoxBg = Color(0xFFF3F4F6)
private val FloorCircleBg = Color(0xFFEAECEE)
private val White = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSellScreen(
    onOpenDrawer: () -> Unit = {}
) {
    val storedEnergyPercent = 0.75f
    val storedEnergyKwh = 12.2f
    var accumulatedCredits by remember { mutableDoubleStateOf(45.20) }
    val totalPowerUsage = 20
    var autoSellEnabled by remember { mutableStateOf(true) }
    val floors = remember { listOf("Floor 1", "Floor 2") }

    // Bottom Sheet State
    var showSellSheet by remember { mutableStateOf(false) }
    var sellAmountKwh by remember { mutableFloatStateOf(5.0f) }
    val tnbRatePerKwh = 0.38 // RM per kWh under ATAP program

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ViewSidebar,
                                contentDescription = "Sidebar",
                                tint = TextDark
                            )
                        }
                        Text(
                            text = "Smart Sell",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextDark
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = CardBorderColor)
            }
        }

        // ---- Main Screen Content ----
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ---- Stored Energy Card ----
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, CardBorderColor),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
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

                            Column {
                                Text(
                                    text = "Stored Energy",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextGray
                                )
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${(storedEnergyPercent * 100).toInt()}%",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = "($storedEnergyKwh kWh)",
                                        fontSize = 14.sp,
                                        color = TextGray
                                    )
                                }
                            }
                        }

                        // Hardware-accelerated Progress Bar
                        LinearProgressIndicator(
                            progress = { storedEnergyPercent },
                            modifier = Modifier
                                .width(110.dp)
                                .height(8.dp)
                                .clip(CircleShape),
                            color = BrandGreenColour,
                            trackColor = ProgressBg,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                // ---- Auto-Sell Excess Electricity Card ----
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, CardBorderColor),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Bolt,
                                    contentDescription = "Bolt",
                                    tint = BrandGreenColour,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Auto-Sell Excess Electricity",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }

                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Auto-Sell Toggle",
                                tint = if (autoSellEnabled) ActiveBlue else TextGray,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable { autoSellEnabled = !autoSellEnabled }
                            )
                        }

                        Text(
                            text = "Automatically sell excess power to TNB under the 1:1 Solar ATAP credit program.",
                            fontSize = 13.sp,
                            color = TextGray,
                            lineHeight = 18.sp
                        )

                        // Accumulated Bill Credits Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CreditBoxBg)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Accumulated Bill Credits",
                                fontSize = 14.sp,
                                color = TextGray
                            )
                            Text(
                                text = "RM ${String.format("%.2f", accumulatedCredits)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreenColour
                            )
                        }

                        // Sell Excess Manually Button
                        OutlinedButton(
                            onClick = { showSellSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CardBorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                        ) {
                            Text(
                                text = "Sell Excess Manually Now",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ---- Power Usage Card ----
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, CardBorderColor),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.AltRoute,
                                contentDescription = "Power Flow",
                                tint = TextDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Power Usage",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        floors.forEachIndexed { index, floor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(FloorCircleBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextGray
                                        )
                                    }
                                    Text(
                                        text = floor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                }

                                Surface(
                                    color = BrandGreenColour,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "Solar",
                                        color = White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(thickness = 1.dp, color = CardBorderColor)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Total : ${totalPowerUsage}kWh/day",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }
    }

    // ---- Manual Sell Modal Bottom Sheet ----
    if (showSellSheet) {
        val estimatedEarnings = sellAmountKwh * tnbRatePerKwh

        ModalBottomSheet(
            onDismissRequest = { showSellSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Manual Energy Discharge",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = "Select how much stored energy to sell to the TNB grid immediately at RM ${tnbRatePerKwh}/kWh.",
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp
                )

                // Energy Amount Slider Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Amount to Sell",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                        Text(
                            text = "${String.format("%.1f", sellAmountKwh)} kWh",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreenColour
                        )
                    }

                    Slider(
                        value = sellAmountKwh,
                        onValueChange = { sellAmountKwh = it },
                        valueRange = 0.5f..storedEnergyKwh,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = BrandGreenColour,
                            activeTrackColor = BrandGreenColour,
                            inactiveTrackColor = ProgressBg
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Min: 0.5 kWh", fontSize = 12.sp, color = TextGray)
                        Text(text = "Max: ${storedEnergyKwh} kWh", fontSize = 12.sp, color = TextGray)
                    }
                }

                // Estimated Return Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CreditBoxBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Bill Credit",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                        Text(
                            text = "1:1 Solar ATAP Rate",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                    Text(
                        text = "+ RM ${String.format("%.2f", estimatedEarnings)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenColour
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showSellSheet = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text(text = "Cancel", color = TextDark, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            accumulatedCredits += estimatedEarnings
                            showSellSheet = false
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreenColour)
                    ) {
                        Text(text = "Discharge Now", fontWeight = FontWeight.Bold, color = White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSmartSellScreen() {
    SmartSellScreen()
}