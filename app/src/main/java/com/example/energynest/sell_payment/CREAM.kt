package com.example.energynest.sell_payment

import com.example.energynest.R
import com.example.energynest.ui.theme.Background
import com.example.energynest.ui.theme.TextDark
import com.example.energynest.ui.theme.TextGray
import com.example.energynest.ui.theme.BrandGreenColour
import com.example.energynest.ui.theme.LightGrayBg
import com.example.energynest.ui.theme.ProgressBg
import com.example.energynest.ui.theme.BorderLight
import com.example.energynest.ui.theme.White

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// Local color definitions removed

@Composable
fun CreamScreen(
    onOpenDrawer: () -> Unit = {},
    onCheckEligibility: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var roofSpace by remember { mutableFloatStateOf(1200f) }

    val minIncome = remember(roofSpace) { (roofSpace * 0.208).roundToInt() }
    val maxIncome = remember(roofSpace) { (roofSpace * 0.333).roundToInt() }
    val formattedRoofSpace = remember(roofSpace) { "${"%,d".format(roofSpace.roundToInt())} sq ft" }

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
                                painter = painterResource(id = R.drawable.sidebar_icon),
                                contentDescription = "Sidebar",
                                tint = TextDark
                            )
                        }
                        Text(
                            text = "CREAM Rooftop Leasing",
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

        // ---- Main Content ----
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Zero Cost Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGrayBg),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(76.dp)
                                .background(BrandGreenColour)
                        )
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(BrandGreenColour),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$",
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Zero Installation Cost. Zero Maintenance.",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    lineHeight = 20.sp
                                )
                                Text(
                                    text = "Earn Rental Income effortlessly by leasing your unused roof space.",
                                    fontSize = 13.sp,
                                    color = TextGray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Calculator Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Calculate Potential Income",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        // Roof Space Slider
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ESTIMATED ROOF SPACE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(LightGrayBg, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = formattedRoofSpace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandGreenColour
                                    )
                                }
                            }

                            Slider(
                                value = roofSpace,
                                onValueChange = { roofSpace = it },
                                valueRange = 500f..2000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = BrandGreenColour,
                                    activeTrackColor = BrandGreenColour,
                                    inactiveTrackColor = ProgressBg
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "500", fontSize = 12.sp, color = TextGray)
                                Text(text = "2,000", fontSize = 12.sp, color = TextGray)
                            }
                        }
                    }
                }

                // Income Result Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "POTENTIAL MONTHLY RENTAL INCOME",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "RM $minIncome - RM $maxIncome",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreenColour,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "*Estimates vary based on roof condition and final assessment.",
                            fontSize = 13.sp,
                            color = TextGray,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // CTA Button (Triggers Navigation)
                Button(
                    onClick = onCheckEligibility,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
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
                            painter = painterResource(id = R.drawable.verified_icon),
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Check Roof Eligibility with LEGA",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreamScreenPreview() {
    CreamScreen()
}