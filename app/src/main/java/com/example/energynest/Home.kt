package com.example.energynest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Colours from Figma ----
private val Background = Color(0xFFF7F9FB)
private val BorderLight = Color(0xFFBBCABF)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF3C4A42)
private val TextGrayLight = Color(0xFF505F76)
private val BrandGreenColour = Color(0xFF10B981)
private val ProgressBg = Color(0xFFE2E8F0)
private val CardBg = Color(0xFFF7F9FB)
private val AvatarBg = Color(0xFFE6E8EA)
private val White = Color.White

@Suppress("DEPRECATION")   // for CircularProgressIndicator progress parameter
@Composable
fun HomeScreen() {
    val storedEnergyPercent = 0.75f
    val solarGenerated = 8.4
    val gridConsumed = 11.6
    val estimatedDuration =
        "Based on current stored energy, your home can run for approximately 12 hours."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Smart Sell",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = TextDark,
                        letterSpacing = (-0.24).sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.33.dp)
                                .background(TextGray)
                        )
                        Text(
                            text = "Peninsular Malaysia",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = TextGray,
                            lineHeight = 20.sp
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
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(TextDark)
                    )
                }
            }

            // 2. Circular Gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(245.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(256.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.fillMaxSize(),
                        color = ProgressBg,
                        strokeWidth = 20.48.dp,
                        trackColor = Color.Transparent
                    )
                    CircularProgressIndicator(
                        progress = storedEnergyPercent,
                        modifier = Modifier.fillMaxSize(),
                        color = BrandGreenColour,
                        strokeWidth = 20.48.dp,
                        trackColor = Color.Transparent,
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.67.dp)
                                .background(BrandGreenColour)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Today",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = TextGray,
                            letterSpacing = 0.7.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "12.2",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                color = TextDark,
                                letterSpacing = (-0.24).sp
                            )
                            Text(
                                text = " kWh",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = TextDark,
                                letterSpacing = (-0.24).sp,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }

            // 3. Stored Energy Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
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
                                text = "12.2",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = TextDark,
                                lineHeight = 28.sp
                            )
                            Text(
                                text = " kWh",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = TextGray,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
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

            // 4. AI Insight
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .border(
                        width = 4.dp,
                        color = BrandGreenColour,
                        shape = RoundedCornerShape(0.dp)
                    )
                    .background(CardBg),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .padding(top = 4.dp)
                            .background(BrandGreenColour)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Estimated Usage Duration",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = TextGray,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = estimatedDuration,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = TextDark,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 5. Stats Grid – weight now resolves because we imported it
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatItem(
                    label = "Solar Generated",
                    value = solarGenerated.toString(),   // .toString() avoids string template warning
                    modifier = Modifier.weight(1f)       // ✅ works with the import
                )
                StatItem(
                    label = "Grid Consumed",
                    value = gridConsumed.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(111.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(TextGray)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                color = TextGray,
                letterSpacing = 0.6.sp
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = TextDark,
                    lineHeight = 28.sp
                )
                Text(
                    text = "kWh",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = TextDark
                )
            }
        }
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(95.dp)
                    .height(51.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(BrandGreenColour),
                contentAlignment = Alignment.Center
            ) {
                NavItem(
                    icon = Icons.Outlined.WbSunny,
                    label = "Home",
                    iconTint = White,
                    textColor = White
                )
            }
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "Smart Sell",
                iconTint = TextGrayLight,
                textColor = TextGrayLight,
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(White)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "CREAM",
                iconTint = TextGrayLight,
                textColor = TextGrayLight,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "Services",
                iconTint = TextGray,
                textColor = TextGray,
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(White)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
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
fun HomeScreenPreview() {
    HomeScreen()
}