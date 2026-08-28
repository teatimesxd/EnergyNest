package com.example.energynest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import kotlin.math.roundToInt

// ---- Colours from Figma ----
private val Background = Color(0xFFF7F9FB)
private val BorderLight = Color(0xFFBBCABF)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF3C4A42)
private val TextGrayLight = Color(0xFF505F76)
private val BrandGreenColour = Color(0xFF10B981)
private val White = Color.White
private val LightGrayBg = Color(0xFFF2F4F6)

@Composable
fun CreamScreen() {
    var propertyType by remember { mutableStateOf("Terrace") }
    var roofSpace by remember { mutableFloatStateOf(1200f) } // in sq ft
    val propertyTypes = listOf("Terrace", "Semi-D", "Bungalow", "Apartment", "Others")

    // Calculate potential monthly income (rough estimate)
    val minIncome = (roofSpace * 0.25).roundToInt()
    val maxIncome = (roofSpace * 0.40).roundToInt()

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
                        text = "CREAM Rooftop Leasing",
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
                HorizontalDivider(thickness = 1.dp, color = BorderLight)
            }

            // ---- Main Content ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(97.dp)
                        .border(4.dp, BrandGreenColour, RoundedCornerShape(0.dp))
                        .background(LightGrayBg),
                    colors = CardDefaults.cardColors(containerColor = LightGrayBg),
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
                                .size(20.dp)
                                .padding(top = 4.dp)
                                .background(BrandGreenColour)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Zero Installation Cost. Zero Maintenance.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = TextDark,
                                lineHeight = 24.sp
                            )
                            Text(
                                text = "Earn Rental Income effortlessly by leasing your unused roof space.",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = TextGray,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Calculator Section
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
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "Calculate Potential Income",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = TextDark
                        )

                        // Property Type Dropdown
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "PROPERTY TYPE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                color = TextGray,
                                letterSpacing = 0.6.sp
                            )
                            // Simple dropdown (using a button that shows options)
                            var expanded by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                    .background(White)
                                    .clickable { expanded = !expanded }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = propertyType,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = TextDark
                                )
                                // Dropdown arrow
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(12.dp)
                                        .background(TextGray)
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                propertyTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            propertyType = type
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Slider for roof space
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ESTIMATED ROOF SPACE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = TextGray,
                                    letterSpacing = 0.6.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(LightGrayBg, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${roofSpace.roundToInt()} sq ft",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif,
                                        color = BrandGreenColour
                                    )
                                }
                            }

                            // Slider
                            Slider(
                                value = roofSpace,
                                onValueChange = { roofSpace = it },
                                valueRange = 500f..5000f,
                                steps = 9, // increments of 500
                                colors = SliderDefaults.colors(
                                    thumbColor = BrandGreenColour,
                                    activeTrackColor = BrandGreenColour
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Min/Max labels
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "500",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = TextGray,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    text = "5000+",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = TextGray,
                                    letterSpacing = 0.6.sp
                                )
                            }
                        }
                    }
                }

                // Output Box – Potential Monthly Rental Income
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Decorative subtle pattern (simulated with a Box)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(BrandGreenColour.copy(alpha = 0.1f))
                        )
                        Text(
                            text = "POTENTIAL MONTHLY RENTAL INCOME",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = TextGray,
                            letterSpacing = 0.6.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "RM $minIncome - RM $maxIncome",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = BrandGreenColour,
                            letterSpacing = (-0.24).sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Estimates vary based on roof condition and final assessment.",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = TextGray,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // CTA Button – fixed at bottom of scrollable content (but above bottom nav)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Button(
                    onClick = { /* Check Eligibility */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreenColour,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WbSunny,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Check Roof Eligibility with LEGA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = White
                        )
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

            // Smart Sell (inactive)
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "Smart Sell",
                iconTint = TextGrayLight,
                textColor = TextGrayLight,
                modifier = Modifier.weight(1f)
            )

            // CREAM (active – green pill)
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
                    label = "CREAM",
                    iconTint = White,
                    textColor = White
                )
            }

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
fun PreviewCreamScreen() {
    CreamScreen()
}