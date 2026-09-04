package com.example.energynest.shared_ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energynest.R
import com.example.energynest.ui.theme.BrandGreenColour
import com.example.energynest.ui.theme.TextGray
import com.example.energynest.ui.theme.White

data class NavItem(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int
)

@Composable
fun AppBottomNavBar(
    currentRoute: String,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("smart_sell", "Smart Sell", R.drawable.sell_icon),
        NavItem("electric_analysis", "Analysis", R.drawable.bar_chart_icon),
        NavItem("home", "Home", R.drawable.home_icon),
        NavItem("cream", "CREAM", R.drawable.solar_power_icon),
        NavItem("services", "Services", R.drawable.build_icon)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val backgroundColor = if (isSelected) BrandGreenColour else Color.Transparent
                val contentColor = if (isSelected) White else TextGray

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = backgroundColor,
                        shape = CircleShape,
                        modifier = Modifier
                            .width(68.dp)
                            .clickable { onNavigateTo(item.route) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.label,
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
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