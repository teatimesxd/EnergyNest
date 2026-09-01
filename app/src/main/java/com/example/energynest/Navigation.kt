package com.example.energynest

import androidx.annotation.DrawableRes

/**
 * All screen routes in the app.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SmartSell : Screen("smart_sell")
    object Cream : Screen("cream")
    object Services : Screen("services")
    object Settings : Screen("settings")
    object TermsAndConditions : Screen("terms_and_conditions")
}

/**
 * Bottom navigation item data using Drawable resources.
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    @DrawableRes val iconRes: Int
)

/**
 * List of bottom navigation items (order matches the bottom bar).
 */
val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", R.drawable.home_icon),
    BottomNavItem(Screen.SmartSell, "Smart Sell", R.drawable.sell_icon),
    BottomNavItem(Screen.Cream, "CREAM", R.drawable.solar_power_icon),
    BottomNavItem(Screen.Services, "Services", R.drawable.build_icon)
)