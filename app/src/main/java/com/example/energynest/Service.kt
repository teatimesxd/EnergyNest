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
import androidx.compose.runtime.Composable
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

// ---- Colours from Figma ----
private val Background = Color(0xFFF7F9FB)
private val BorderLight = Color(0xFFBBCABF)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF3C4A42)
private val BrandGreenColour = Color(0xFF10B981)
private val White = Color.White
private val IconBg = Color(0xFFECEEF0)
private val DarkGreen = Color(0xFF006C49)

@Composable
fun ServicesScreen() {
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
                        text = "Services",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
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

            // ---- Service Cards Grid (2 columns) ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ServiceCard(
                        title = "Customer Service",
                        description = "Get help with your account, billing inquiries, and general support for...",
                        buttonText = "CONTACT US",
                        iconColor = DarkGreen,
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCard(
                        title = "Consultation",
                        description = "Schedule a session with our energy experts to optimize your home for...",
                        buttonText = "BOOK SESSION",
                        iconColor = DarkGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ServiceCard(
                        title = "Maintenance",
                        description = "Regular check-ups for your solar panels and battery storage to ensure",
                        buttonText = "SCHEDULE CHECK",
                        iconColor = DarkGreen,
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCard(
                        title = "Cleaning",
                        description = "Professional cleaning for solar arrays to maintain optimal sunlight...",
                        buttonText = "BOOK CLEANING",
                        iconColor = DarkGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---- Common Questions Section ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Common Questions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = TextDark
                )

                val faqs = listOf(
                    "How to track savings?",
                    "What is CREAM?",
                    "Service response time?"
                )
                faqs.forEach { question ->
                    FAQItem(question = question)
                }

                // ✅ Fixed OutlinedButton – no extra border parameters
                OutlinedButton(
                    onClick = { /* View all FAQs */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(39.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkGreen,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "View All FAQs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        color = DarkGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ---- Bottom Navigation ----
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ServiceCard(
    title: String,
    description: String,
    buttonText: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(155.dp)
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .background(White),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(IconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(iconColor)
                    )
                }
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = TextDark,
                    lineHeight = 18.sp
                )
            }

            Text(
                text = description,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif,
                color = TextGray,
                lineHeight = 15.sp,
                maxLines = 3,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { /* action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(29.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreenColour,
                    contentColor = White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.275.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FAQItem(question: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(47.dp)
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .background(White),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                color = TextDark,
                lineHeight = 21.sp
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(TextGray)
            )
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
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(0.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "Home",
                iconTint = TextGray,
                textColor = TextGray,
                modifier = Modifier.weight(1f)
            )
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "Smart Sell",
                iconTint = TextGray,
                textColor = TextGray,
                modifier = Modifier.weight(1f)
            )
            NavItem(
                icon = Icons.Outlined.WbSunny,
                label = "CREAM",
                iconTint = TextGray,
                textColor = TextGray,
                modifier = Modifier.weight(1f)
            )
            // Services (active – green pill)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(BrandGreenColour)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                NavItem(
                    icon = Icons.Outlined.WbSunny,
                    label = "Services",
                    iconTint = White,
                    textColor = White
                )
            }
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
fun PreviewServicesScreen() {
    ServicesScreen()
}