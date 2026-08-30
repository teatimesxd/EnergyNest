package com.example.energynest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TextGray = Color(0xFF5A6065)
private val BrandGreenColour = Color(0xFF00B87C)
private val White = Color.White

data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun AppBottomNavBar(
    currentRoute: String,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("home", "Home", Icons.Outlined.Home),
        NavItem("smart_sell", "Smart Sell", Icons.Outlined.LocalOffer),
        NavItem("cream", "CREAM", Icons.Outlined.BarChart),
        NavItem("services", "Services", Icons.Outlined.Build)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val backgroundColor = if (isSelected) BrandGreenColour else Color.Transparent
                val contentColor = if (isSelected) White else TextGray

                // Equal weight container guarantees even spacing across screens
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = backgroundColor,
                        shape = CircleShape,
                        modifier = Modifier
                            .width(76.dp) // Fixed width for uniform capsule sizes
                            .clickable { onNavigateTo(item.route) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = contentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = contentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}