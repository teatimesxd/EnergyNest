package com.example.energynest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.energynest.ui.components.AppBottomNavBar
import com.example.energynest.ui.theme.EnergyNestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnergyNestTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.SmartSell.route,
                    Screen.Cream.route,
                    Screen.Services.route
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        SidebarDrawerContent(
                            currentRoute = currentRoute,
                            onCloseDrawer = { scope.launch { drawerState.close() } },
                            onNavigate = { route: String ->
                                scope.launch { drawerState.close() }
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                AppBottomNavBar(
                                    currentRoute = currentRoute ?: Screen.Home.route,
                                    onNavigateTo = { route: String ->
                                        if (currentRoute != route) {
                                            navController.navigate(route) {
                                                popUpTo(Screen.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }
                            composable(Screen.SmartSell.route) {
                                SmartSellScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }
                            composable(Screen.Cream.route) {
                                CreamScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onCheckEligibility = {
                                        navController.navigate("lega_eligibility")
                                    }
                                )
                            }
                            composable(Screen.Services.route) {
                                ServicesScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }
                            composable("lega_eligibility") {
                                LegaEligibilityScreen(
                                    onBack = { navController.popBackStack() },
                                    onCompleteAssessment = { navController.popBackStack() }
                                )
                            }
                            composable("electric_analysis") {
                                ElectricAnalysisScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            // Payment History Route
                            composable("payment_history") {
                                PaymentHistoryScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}