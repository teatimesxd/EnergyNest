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
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                if (isSelected) {
                    Surface(
                        color = BrandGreenColour,
                        shape = CircleShape,
                        modifier = Modifier.clickable { onNavigateTo(item.route) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(item.icon, contentDescription = item.label, tint = White, modifier = Modifier.size(18.dp))
                            Text(item.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateTo(item.route) }
                    ) {
                        Icon(item.icon, contentDescription = item.label, tint = TextGray)
                        Text(item.label, fontSize = 12.sp, color = TextGray)
                    }
                }
            }
        }
    }
}