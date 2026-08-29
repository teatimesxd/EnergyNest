package com.example.energynest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewSidebar
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.HomeRepairService
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreenColour = Color(0xFF00B87C)
private val White = Color.White
private val IconBg = Color(0xFFE8ECE9)
private val BorderLight = Color(0xFFE2E8F0)

@Composable
fun ServicesScreen(
    onOpenDrawer: () -> Unit = {}
) {
    // Cache static lists to eliminate heap allocations on recomposition
    val faqs = remember {
        listOf(
            "How to track savings?",
            "What is CREAM?",
            "Service response time?"
        )
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
                            text = "Services",
                            fontSize = 19.sp,
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
                HorizontalDivider(thickness = 1.dp, color = BorderLight)
            }
        }

        // Service Cards Grid
        item {
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
                        icon = Icons.Outlined.Headset,
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCard(
                        title = "Consultation",
                        description = "Schedule a session with our energy experts to optimize your home for...",
                        buttonText = "BOOK SESSION",
                        icon = Icons.Outlined.Handyman,
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
                        icon = Icons.Outlined.HomeRepairService,
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCard(
                        title = "Cleaning",
                        description = "Professional cleaning for solar arrays to maintain optimal sunlight...",
                        buttonText = "BOOK CLEANING",
                        icon = Icons.Outlined.CleaningServices,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // FAQs Section Header
        item {
            Text(
                text = "Common Questions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Lazy FAQ Items
        items(faqs) { question ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                FAQItem(question = question)
            }
        }

        // View All Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedButton(
                    onClick = { /* View all FAQs */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BrandGreenColour),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandGreenColour,
                        containerColor = White
                    )
                ) {
                    Text(
                        text = "View All FAQs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenColour
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    description: String,
    buttonText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(IconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = BrandGreenColour,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = description,
                fontSize = 12.sp,
                color = TextGray,
                lineHeight = 16.sp,
                minLines = 3,
                maxLines = 3
            )

            Button(
                onClick = { /* action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreenColour,
                    contentColor = White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FAQItem(question: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = TextDark
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceScreenPreview(){
    ServicesScreen()
}