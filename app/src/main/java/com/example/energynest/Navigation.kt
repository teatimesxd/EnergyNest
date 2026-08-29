package com.example.energynest

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * All screen routes in the app.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SmartSell : Screen("smart_sell")
    object Cream : Screen("cream")
    object Services : Screen("services")
}

/**
 * Bottom navigation item data.
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

/**
 * List of bottom navigation items (order matches the bottom bar).
 */
val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Outlined.WbSunny),
    BottomNavItem(Screen.SmartSell, "Smart Sell", Icons.Outlined.WbSunny),
    BottomNavItem(Screen.Cream, "CREAM", Icons.Outlined.WbSunny),
    BottomNavItem(Screen.Services, "Services", Icons.Outlined.WbSunny)
)