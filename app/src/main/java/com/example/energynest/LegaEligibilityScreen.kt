package com.example.energynest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreen = Color(0xFF00B87C)
private val BorderLight = Color(0xFFE2E8F0)
private val White = Color.White
private val LightGreenBg = Color(0xFFD8F3E5)

@Composable
fun LegaEligibilityScreen(
    onBack: () -> Unit = {},
    onCompleteAssessment: () -> Unit = {}
) {
    var address by remember { mutableStateOf("") }
    var shadingLevel by remember { mutableStateOf("Low (Full Sun)") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var isEligible by remember { mutableStateOf<Boolean?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    enabled = !isAnalyzing
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark
                    )
                }
                Text(
                    text = "LEGA Roof Assessment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
            HorizontalDivider(thickness = 1.dp, color = BorderLight)
        }

        // Form Body
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreenBg)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BrandGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "LEGA Roof Scanning",
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Enter property details to calculate solar yield and rooftop eligibility.",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = {
                        address = it
                        if (isEligible != null) isEligible = null
                    },
                    label = { Text("Property Address / Postcode") },
                    leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAnalyzing,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        unfocusedContainerColor = White,
                        focusedContainerColor = White
                    ),
                    singleLine = true
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ROOF SHADING EXPOSURE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )
                    listOf("Low (Full Sun)", "Moderate (Partial Trees/Buildings)", "Heavy Shading").forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(White)
                                .border(
                                    width = 1.dp,
                                    color = if (shadingLevel == level) BrandGreen else BorderLight,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = !isAnalyzing) {
                                    shadingLevel = level
                                    if (isEligible != null) isEligible = null
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = TextGray)
                                Text(text = level, fontSize = 14.sp, color = TextDark)
                            }
                            RadioButton(
                                selected = (shadingLevel == level),
                                onClick = {
                                    shadingLevel = level
                                    if (isEligible != null) isEligible = null
                                },
                                enabled = !isAnalyzing,
                                colors = RadioButtonDefaults.colors(selectedColor = BrandGreen)
                            )
                        }
                    }
                }

                // Progress Indicator
                if (isAnalyzing) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = BrandGreen)
                            Text(
                                text = "LEGA scanning rooftop satellite data...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark
                            )
                        }
                    }
                } else if (isEligible == true) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Roof Qualifies for CREAM Leasing!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Estimated Solar Yield: 94% Optimal. Potential earnings up to RM 350/month.",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (isEligible == true) {
                            onCompleteAssessment()
                        } else {
                            coroutineScope.launch {
                                isAnalyzing = true
                                delay(2500)
                                isAnalyzing = false
                                isEligible = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = address.isNotBlank() && !isAnalyzing,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text(
                        text = when {
                            isAnalyzing -> "Analyzing..."
                            isEligible == true -> "Submit Leasing Application"
                            else -> "Analyze Roof via LEGA"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LegaEligibilityScreenPreview() {
    LegaEligibilityScreen()
}