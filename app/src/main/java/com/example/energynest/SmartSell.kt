package com.example.energynest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// ---- Colours from Figma ----
private val Background = Color(0xFFF7F9FB)
private val BorderLight = Color(0xFFBBCABF)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF3C4A42)
private val TextGrayLight = Color(0xFF505F76)
private val BrandGreenColour = Color(0xFF10B981)
private val ProgressBg = Color(0xFFE0E3E5)
private val CardBg = Color(0xFFF7F9FB)
private val White = Color.White
private val LightGreenBg = Color(0xFFF2F4F6)

@Composable
fun SmartSellScreen() {
    // Static data
    val storedEnergyPercent = 0.75f
    val storedEnergyKwh = 12.2
    val accumulatedCredits = 45.20
    val totalPowerUsage = 20
    var autoSellEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)         // space for bottom nav
                .verticalScroll(rememberScrollState())
        ) {
            // ---- Top App Bar ----
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Background.copy(alpha = 0.9f))
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = { /* navigate back */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = "Smart Sell",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        color = TextDark
                    )
                    IconButton(onClick = { /* notifications */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
                // Bottom border
                HorizontalDivider(
                    thickness = 1.dp,
                    color = BorderLight
                )
            }

            // ---- Main Content ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Stored Energy Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(BrandGreenColour.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(13.dp)
                                        .background(BrandGreenColour)
                                )
                            }
                            Column {
                                Text(
                                    text = "Stored Energy",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = TextGray,
                                    letterSpacing = 0.6.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${(storedEnergyPercent * 100).toInt()}%",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.SansSerif,
                                        color = TextDark,
                                        lineHeight = 28.sp
                                    )
                                    Text(
                                        text = " ($storedEnergyKwh kWh)",
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        color = TextGray
                                    )
                                }
                            }
                        }
                        // Progress bar
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(ProgressBg)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = storedEnergyPercent)
                                    .fillMaxSize()
                                    .background(BrandGreenColour)
                            )
                        }
                    }
                }

                // 2. Auto-Sell Excess Electricity
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header with toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(BrandGreenColour)
                                )
                                Text(
                                    text = "Auto-Sell Excess Electricity",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = TextDark
                                )
                            }
                            Switch(
                                checked = autoSellEnabled,
                                onCheckedChange = { autoSellEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = White,
                                    checkedTrackColor = BrandGreenColour,
                                    uncheckedThumbColor = White,
                                    uncheckedTrackColor = TextGrayLight
                                )
                            )
                        }

                        // Description
                        Text(
                            text = "Automatically sell excess power to TNB under the 1:1 Solar ATAP credit program.",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = TextGray,
                            lineHeight = 23.sp
                        )

                        // Accumulated Bill Credits
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGreenBg)
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Accumulated Bill Credits",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = TextGray
                            )
                            Text(
                                text = "RM ${String.format(Locale.US, "%.2f", accumulatedCredits)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                color = Color(0xFF006C49)
                            )
                        }

                        // Sell Excess Manually Now button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Background)
                                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sell Excess Manually Now",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = TextDark
                            )
                        }
                    }
                }

                // 3. Power Usage
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(TextGrayLight)
                            )
                            Text(
                                text = "Power Usage",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                color = TextDark
                            )
                        }

                        // Floor items
                        val floors = listOf("Floor 1", "Floor 2")
                        floors.forEachIndexed { index, floor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardBg)
                                    .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFECEEF0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0x80000000)
                                        )
                                    }
                                    Text(
                                        text = floor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.SansSerif,
                                        color = TextDark
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(BrandGreenColour, RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Solar",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.SansSerif,
                                        color = White,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                            }
                        }

                        // Total
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Total : $totalPowerUsage kWh/day",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }

        // ---- Bottom Navigation ----
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun BottomNavBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(White)
            .border(width = 1.dp, color = BorderLight)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(0.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home (inactive)
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "Home",
                iconTint = TextGrayLight,
                textColor = TextGrayLight,
                modifier = Modifier.weight(1f)
            )

            // Smart Sell (active – green pill)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(BrandGreenColour)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                NavItem(
                    icon = Icons.Outlined.WbSunny,
                    label = "Smart Sell",
                    iconTint = White,
                    textColor = White
                )
            }

            // CREAM (inactive)
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "CREAM",
                iconTint = TextGrayLight,
                textColor = TextGrayLight,
                modifier = Modifier.weight(1f)
            )

            // Services (inactive)
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "Services",
                iconTint = TextGray,
                textColor = TextGray,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            color = textColor,
            letterSpacing = 0.6.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSmartSellScreen() {
    SmartSellScreen()
}